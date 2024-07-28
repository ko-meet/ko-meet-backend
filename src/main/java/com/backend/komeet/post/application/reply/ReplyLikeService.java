package com.backend.komeet.post.application.reply;

import com.backend.komeet.infrastructure.components.RedisDistributedLock;
import com.backend.komeet.post.model.entities.Reply;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.COMMENT_NOT_FOUND;

/**
 * 대댓글 좋아요 관련 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReplyLikeService {
    private final ReplyRepository replyRepository;
    private final RedisDistributedLock redisDistributedLock;
    final String LOCK_KEY = "likeReply : ";

    /**
     * 대댓글 좋아요를 처리하는 메서드
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void likeReply(
            Long userSeq,
            Long replySeq
    ) {
        int maxRetries = 3;
        int retries = 0;
        long retryIntervalMillis = 1000; // 1초 간격으로 재시도
        Integer likeCount = null;

        while (retries <= maxRetries) {
            try {
                likeCount = processLike(userSeq, replySeq);
                if (likeCount != null) {
                    break;
                } else {
                    log.error("Failed to acquire lock for postSeq: {}", replySeq);
                }
            } catch (CustomException e) {
                log.error(e.getMessage());
            }
            if (retries < maxRetries) {
                retries++;
                try {
                    Thread.sleep(retryIntervalMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (likeCount == null) {
            log.error(
                    "Failed to process like for postSeq {} after {} retries",
                    replySeq,
                    maxRetries
            );
        }
    }

    /**
     * 좋아요 작업을 처리하는 메서드
     */
    private Integer processLike(
            Long userSeq,
            Long replySeq
    ) throws CustomException {
        Reply reply = getReply(replySeq);

        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.acquireLock(
                    LOCK_KEY, replySeq.toString(), 10
            );
            if (lockAcquired) {
                if (reply.getLikeUsers().remove(userSeq)) {
                    reply.setUpVotes(reply.getUpVotes() - 1);
                } else {
                    reply.getLikeUsers().add(userSeq);
                    reply.setUpVotes(reply.getUpVotes() + 1);
                }
                return replyRepository.save(reply).getUpVotes();
            } else {
                return null;
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(LOCK_KEY, replySeq.toString());
            }
        }
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     */
    private Reply getReply(
            Long seq
    ) {
        return replyRepository
                .findById(seq)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }
}
