package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.entities.PostResource;
import com.backend.immilog.post.model.repositories.BulkInsertRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.services.PostUploadService;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.backend.immilog.post.exception.PostErrorCode.FAILED_TO_SAVE_POST;
import static com.backend.immilog.post.model.enums.PostType.POST;
import static com.backend.immilog.post.model.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.model.enums.ResourceType.TAG;
import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostUploadServiceImpl implements PostUploadService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BulkInsertRepository bulkInsertRepository;

    @Override
    public void uploadPost(
            Long userSeq,
            PostUploadRequest postUploadRequest
    ) {
        User user = getUser(userSeq);
        Post post = postRepository.save(
                createPostEntity(userSeq, postUploadRequest, user)
        );
        Long postSeq = post.getSeq();
        insertAllPostResources(postUploadRequest, postSeq);
    }

    private static Post createPostEntity(
            Long userSeq,
            PostUploadRequest postUploadRequest,
            User user
    ) {
        return Post.of(
                postUploadRequest,
                user
        );
    }

    private void insertAllPostResources(
            PostUploadRequest postUploadRequest,
            Long postSeq
    ) {
        List<PostResource> postResourceList = getPostResourceList(
                postUploadRequest,
                postSeq
        );
        bulkInsertRepository.saveAll(
                postResourceList,
                """
                INSERT INTO post_resource (
                    post_seq,
                    post_type,
                    resource_type,
                    content
                ) VALUES (?, ?, ?, ?)
                """,
                (ps, postResource) -> {
                    try {
                        ps.setLong(1, postResource.getPostSeq());
                        ps.setString(2, postResource.getPostType().name());
                        ps.setString(3, postResource.getResourceType().name());
                        ps.setString(4, postResource.getContent());
                    } catch (SQLException e) {
                        log.error("Failed to save post resource: {}", e.getMessage());
                        throw new CustomException(FAILED_TO_SAVE_POST);
                    }
                }
        );
    }

    private List<PostResource> getPostResourceList(
            PostUploadRequest postUploadRequest,
            Long postSeq
    ) {
        List<PostResource> postResources = new ArrayList<>();
        postResources.addAll(getTagEntities(postUploadRequest, postSeq));
        postResources.addAll(getAttachmentEntities(postUploadRequest, postSeq));
        return Collections.unmodifiableList(postResources);
    }

    private List<PostResource> getTagEntities(
            PostUploadRequest postUploadRequest,
            Long postSeq
    ) {
        if (postUploadRequest.tags() == null) {
            return List.of();
        }
        return postUploadRequest
                .tags()
                .stream()
                .map(tag -> PostResource.of(POST, TAG, tag, postSeq))
                .toList();
    }

    private List<PostResource> getAttachmentEntities(
            PostUploadRequest postUploadRequest,
            Long postSeq
    ) {
        if (postUploadRequest.attachments() == null) {
            return List.of();
        }
        return postUploadRequest
                .attachments()
                .stream()
                .map(url -> PostResource.of(POST, ATTACHMENT, url, postSeq))
                .toList();
    }

    private User getUser(
            Long userSeq
    ) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

}
