package com.backend.komeet.post.application.post;

import com.backend.komeet.global.components.RedisDistributedLock;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import com.backend.komeet.post.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.*;

/**
 * 게시물 좋아요 관련 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PostLikeService {
    private final PostRepository postRepository;
    private final RedisDistributedLock redisDistributedLock;
    final String LOCK_KEY = "likePost : ";

    /**
     * 게시물 좋아요를 처리하는 메서드
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void likePost(
            Long userSeq,
            Long postSeq
    ) {
        int maxRetries = 3;
        int retries = 0;
        long retryIntervalMillis = 1000; // 1초 간격으로 재시도
        Long likeCount = null;

        while (retries <= maxRetries) {
            try {
                likeCount = processLike(userSeq, postSeq);
                if (likeCount != null) {
                    break;
                } else {
                    log.error("Failed to acquire lock for postSeq: {}", postSeq);
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
            log.error("Failed to process like for postSeq {} after {} retries", postSeq, maxRetries);
        }
    }

    /**
     * 좋아요 작업을 처리하는 메서드
     */
    private Long processLike(
            Long userSeq,
            Long postSeq
    ) throws CustomException {
        Post post = getPost(postSeq);

        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.acquireLock(
                    LOCK_KEY, postSeq.toString(), 10
            );
            if (lockAcquired) {
                PostMetaData postMetaData = post.getPostMetaData();
                if (postMetaData.getLikeUsers().remove(userSeq)) {
                    postMetaData.setLikeCount(postMetaData.getLikeCount() - 1);
                } else {
                    postMetaData.getLikeUsers().add(userSeq);
                    postMetaData.setLikeCount(postMetaData.getLikeCount() + 1);
                }
                return postRepository.save(post).getPostMetaData().getLikeCount();
            } else {
                return null;
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(LOCK_KEY, postSeq.toString());
            }
        }
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     */
    private Post getPost(
            Long seq
    ) {
        return postRepository
                .findById(seq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }
}
