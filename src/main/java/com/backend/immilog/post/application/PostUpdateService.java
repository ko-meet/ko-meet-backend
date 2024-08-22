package com.backend.immilog.post.application;

import com.backend.immilog.global.application.RedisDistributedLock;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.infrastructure.BulkInsertRepository;
import com.backend.immilog.post.enums.ResourceType;
import com.backend.immilog.post.infrastructure.InteractionUserRepository;
import com.backend.immilog.post.infrastructure.PostRepository;
import com.backend.immilog.post.infrastructure.PostResourceRepository;
import com.backend.immilog.post.model.entities.InteractionUser;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.backend.immilog.global.exception.ErrorCode.*;
import static com.backend.immilog.post.enums.InteractionType.LIKE;
import static com.backend.immilog.post.enums.PostType.POST;
import static com.backend.immilog.post.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.enums.ResourceType.TAG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateService {
    private static final String POST_TYPE = POST.toString();
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final BulkInsertRepository bulkInsertRepository;
    private final InteractionUserRepository interactionUserRepository;

    private final RedisDistributedLock redisDistributedLock;
    final String LIKE_LOCK_KEY = "likePost : ";
    final String VIEW_LOCK_KEY = "viewPost : ";

    @Transactional
    public void updatePost(
            Long userId,
            Long postSeq,
            PostUpdateRequest postUpdateRequest
    ) {
        Post post = getPost(postSeq);
        validateAuthor(userId, post);
        updatePostMetaData(post, postUpdateRequest);
        updateResources(postSeq, postUpdateRequest);
    }

    @Async
    @Transactional
    public void increaseViewCount(Long postSeq) {
        executeWithLock(
                VIEW_LOCK_KEY,
                postSeq.toString(),
                () -> {
                    Post post = getPost(postSeq);
                    Long currentViewCount = post.getPostMetaData().getViewCount();
                    post.getPostMetaData().setViewCount(currentViewCount + 1);
                }
        );
    }

    @Async
    @Transactional
    public void likePost(
            Long userSeq,
            Long postSeq
    ) {
        executeWithLock(
                LIKE_LOCK_KEY,
                postSeq.toString(),
                () -> {
                    List<InteractionUser> likeUsers = getLikeUsers(postSeq);
                    likeUsers.stream()
                            .filter(likeUser -> likeUser.getUserSeq().equals(userSeq))
                            .findAny()
                            .ifPresentOrElse(
                                    interactionUserRepository::delete,
                                    () -> {
                                        InteractionUser likeUser = createLikeUser(userSeq, postSeq);
                                        interactionUserRepository.save(likeUser);
                                    }
                            );
                }
        );
    }

    private void executeWithLock(
            String lockKey,
            String subKey,
            Runnable action
    ) {
        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.tryAcquireLock(lockKey, subKey);
            if (lockAcquired) {
                action.run();
            } else {
                log.error("Failed to acquire lock for {}, key: {}", lockKey, subKey);
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(lockKey, subKey);
            }
        }
    }

    private static InteractionUser createLikeUser(
            Long userSeq,
            Long postSeq
    ) {
        return InteractionUser.of(postSeq, POST, LIKE, userSeq);
    }

    private List<InteractionUser> getLikeUsers(
            Long postSeq
    ) {
        return interactionUserRepository.findByPostSeq(postSeq);
    }

    private void updateResources(
            Long postSeq,
            PostUpdateRequest postUpdateRequest
    ) {
        updateResource(postSeq, postUpdateRequest.getDeleteTags(), postUpdateRequest.getAddTags(), TAG);
        updateResource(postSeq, postUpdateRequest.getDeleteAttachments(), postUpdateRequest.getAddAttachments(), ATTACHMENT);
    }

    private void updateResource(
            Long postSeq,
            List<String> deleteResources,
            List<String> addResources,
            ResourceType resourceType
    ) {
        deleteResourceIfExists(postSeq, deleteResources, resourceType);
        addResourceIfExists(postSeq, addResources, resourceType);
    }

    private void deleteResourceIfExists(
            Long postSeq,
            List<String> deleteResources,
            ResourceType resourceType
    ) {
        if (deleteResources != null && !deleteResources.isEmpty()) {
            postResourceRepository.deleteAllEntities(postSeq, resourceType, deleteResources);
        }
    }

    private void addResourceIfExists(
            Long postSeq,
            List<String> addResources,
            ResourceType resourceType
    ) {
        if (addResources != null && !addResources.isEmpty()) {
            bulkInsertRepository.saveAll(
                    addResources,
                    """
                            INSERT INTO post_resource (
                                post_seq,
                                post_type,
                                resource_type,
                                content
                            ) VALUES (?, ?, ?, ?)
                            """,
                    (ps, resource) -> {
                        try {
                            ps.setLong(1, postSeq);
                            ps.setString(2, POST_TYPE);
                            ps.setString(3, resourceType.name());
                            ps.setString(4, resource);
                        } catch (Exception e) {
                            log.error("Failed to save post resource", e);
                            throw new CustomException(FAILED_TO_SAVE_POST);
                        }
                    }
            );
        }
    }

    private void updatePostMetaData(
            Post post,
            PostUpdateRequest postUpdateRequest
    ) {
        if (postUpdateRequest.getTitle() != null) {
            post.getPostMetaData().setTitle(postUpdateRequest.getTitle());
        }
        if (postUpdateRequest.getContent() != null) {
            post.getPostMetaData().setContent(postUpdateRequest.getContent());
        }
        if (postUpdateRequest.getIsPublic() != null) {
            post.setIsPublic(postUpdateRequest.getIsPublic() ? "Y" : "N");
        }
    }

    private void validateAuthor(
            Long userId,
            Post post
    ) {
        if (!Objects.equals(post.getPostUserData().getUserSeq(), userId)) {
            throw new CustomException(NO_AUTHORITY);
        }
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository.findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }
}
