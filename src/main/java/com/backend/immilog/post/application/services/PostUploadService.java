package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.command.PostUploadCommand;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.entities.PostResource;
import com.backend.immilog.post.model.repositories.BulkInsertRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PostUploadService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BulkInsertRepository bulkInsertRepository;

    @Transactional
    public void uploadPost(
            Long userSeq,
            PostUploadCommand postUploadCommand
    ) {
        User user = getUser(userSeq);
        Post post = postRepository.save(
                createPostEntity(userSeq, postUploadCommand, user)
        );
        Long postSeq = post.getSeq();
        insertAllPostResources(postUploadCommand, postSeq);
    }

    private static Post createPostEntity(
            Long userSeq,
            PostUploadCommand postUploadCommand,
            User user
    ) {
        return Post.of(
                postUploadCommand,
                user
        );
    }

    private void insertAllPostResources(
            PostUploadCommand postUploadCommand,
            Long postSeq
    ) {
        List<PostResource> postResourceList = getPostResourceList(
                postUploadCommand,
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
                        throw new PostException(FAILED_TO_SAVE_POST);
                    }
                }
        );
    }

    private List<PostResource> getPostResourceList(
            PostUploadCommand postUploadCommand,
            Long postSeq
    ) {
        List<PostResource> postResources = new ArrayList<>();
        postResources.addAll(getTagEntities(postUploadCommand, postSeq));
        postResources.addAll(getAttachmentEntities(postUploadCommand, postSeq));
        return Collections.unmodifiableList(postResources);
    }

    private List<PostResource> getTagEntities(
            PostUploadCommand postUploadCommand,
            Long postSeq
    ) {
        if (postUploadCommand.tags() == null) {
            return List.of();
        }
        return postUploadCommand
                .tags()
                .stream()
                .map(tag -> PostResource.of(POST, TAG, tag, postSeq))
                .toList();
    }

    private List<PostResource> getAttachmentEntities(
            PostUploadCommand postUploadCommand,
            Long postSeq
    ) {
        if (postUploadCommand.attachments() == null) {
            return List.of();
        }
        return postUploadCommand
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
                .orElseThrow(() -> new PostException(USER_NOT_FOUND));
    }

}