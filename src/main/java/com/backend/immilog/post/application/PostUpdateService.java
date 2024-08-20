package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.infrastructure.BulkInsertRepository;
import com.backend.immilog.post.enums.PostType;
import com.backend.immilog.post.enums.ResourceType;
import com.backend.immilog.post.infrastructure.PostRepository;
import com.backend.immilog.post.infrastructure.PostResourceRepository;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.backend.immilog.global.exception.ErrorCode.*;
import static com.backend.immilog.post.enums.ResourceType.ATTACHMENT;
import static com.backend.immilog.post.enums.ResourceType.TAG;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostUpdateService {
    private static final String POST_TYPE = PostType.POST.toString();
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;
    private final BulkInsertRepository bulkInsertRepository;

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

    private void updateResources(
            Long postSeq,
            PostUpdateRequest postUpdateRequest
    ) {
        List<String> deleteTags = postUpdateRequest.getDeleteTags();
        List<String> addTags = postUpdateRequest.getAddTags();
        List<String> deleteAttachments = postUpdateRequest.getDeleteAttachments();
        List<String> addAttachments = postUpdateRequest.getAddAttachments();
        updateResource(postSeq, deleteTags, addTags, TAG);
        updateResource(postSeq, deleteAttachments, addAttachments, ATTACHMENT);
    }

    private void updateResource(
            Long postSeq,
            List<String> deleteResources,
            List<String> addResources,
            ResourceType resourceType
    ) {
        if (deleteResources != null && !deleteResources.isEmpty()) {
            postResourceRepository.deleteAllEntities(postSeq, resourceType, deleteResources);
        }

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
                            ps.setLong(2, postSeq);
                            ps.setString(1, POST_TYPE);
                            ps.setString(4, resourceType.name());
                            ps.setString(3, resource);
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
        if (!Objects.equals(post.getUserSeq(), userId)) {
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
