package com.backend.komeet.service.comment;

import com.backend.komeet.component.RedisDistributedLock;
import com.backend.komeet.domain.Comment;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.exception.ErrorCode.COMMENT_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentLikeService {
    private final CommentRepository commentRepository;
    private final RedisDistributedLock redisDistributedLock;
    final String LOCK_KEY = "likeComment : ";

    /**
     * 대댓글 좋아요를 처리하는 메서드
     *
     * @param userSeq  사용자 식별자
     * @param commentSeq 대댓글 식별자
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void likeComment(Long userSeq, Long commentSeq) {
        int maxRetries = 3;
        int retries = 0;
        long retryIntervalMillis = 1000; // 1초 간격으로 재시도
        Integer likeCount = null;

        while (retries <= maxRetries) {
            try {
                likeCount = processLike(userSeq, commentSeq);
                if (likeCount != null) {
                    break;
                } else {
                    log.error("Failed to acquire lock for commentSeq: {}", commentSeq);
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
            log.error("Failed to process like for commentSeq {} after {} retries", commentSeq, maxRetries);
        }
    }

    /**
     * 좋아요 작업을 처리하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @param commentSeq 게시물 식별자
     * @return 작업 성공 여부
     * @throws CustomException 작업 실패
     */
    private Integer processLike(Long userSeq, Long commentSeq) throws CustomException {
        Comment comment = getComment(commentSeq);

        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.acquireLock(
                    LOCK_KEY, commentSeq.toString(), 10
            );
            if (lockAcquired) {
                if (comment.getLikeUsers().remove(userSeq)) {
                    comment.setUpVotes(comment.getUpVotes() - 1);
                } else {
                    comment.getLikeUsers().add(userSeq);
                    comment.setUpVotes(comment.getUpVotes() + 1);
                }
                return commentRepository.save(comment).getUpVotes();
            } else {
                return null;
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(LOCK_KEY, commentSeq.toString());
            }
        }
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     *
     * @param seq 게시물 식별자
     * @return 게시물
     */
    private Comment getComment(Long seq) {
        return commentRepository.findById(seq)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }
}
