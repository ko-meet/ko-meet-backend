package com.backend.immilog.post.application;

import com.backend.immilog.global.application.RedisDistributedLock;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.model.entities.InteractionUser;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.enums.ResourceType;
import com.backend.immilog.post.model.repositories.BulkInsertRepository;
import com.backend.immilog.post.model.repositories.InteractionUserRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.repositories.PostResourceRepository;
import com.backend.immilog.post.model.services.PostUpdateService;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.backend.immilog.post.exception.PostErrorCode.*;
import static com.backend.immilog.post.model.enums.InteractionType.LIKE;
import static com.backend.immilog.post.model.enums.PostType.POST;
import static com.backend.immilog.post.model.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.model.enums.ResourceType.TAG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateServiceImpl implements PostUpdateService {
    private static final String POST_TYPE = POST.toString();
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final BulkInsertRepository bulkInsertRepository;
    private final InteractionUserRepository interactionUserRepository;

    private final RedisDistributedLock redisDistributedLock;
    final String LIKE_LOCK_KEY = "likePost : ";
    final String VIEW_LOCK_KEY = "viewPost : ";

    @Override
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

    @Override
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

    @Override
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
            PostUpdateRequest request
    ) {
        updateResource(postSeq, request.deleteTags(), request.addTags(), TAG);
        updateResource(postSeq, request.deleteAttachments(), request.addAttachments(), ATTACHMENT);
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
                            throw new PostException(FAILED_TO_SAVE_POST);
                        }
                    }
            );
        }
    }

    private void updatePostMetaData(
            Post post,
            PostUpdateRequest request
    ) {
        if (request.title() != null) {
            post.getPostMetaData().setTitle(request.title());
        }
        if (request.content() != null) {
            post.getPostMetaData().setContent(request.content());
        }
        if (request.isPublic() != null) {
            post.setIsPublic(request.isPublic() ? "Y" : "N");
        }
    }

    private void validateAuthor(
            Long userId,
            Post post
    ) {
        if (!Objects.equals(post.getPostUserData().getUserSeq(), userId)) {
            throw new PostException(NO_AUTHORITY);
        }
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository.findById(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
