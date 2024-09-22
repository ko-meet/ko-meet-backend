package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;

import java.util.List;

public interface PostResourceRepository {
    void deleteAllEntities(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostSeq(
            Long seq
    );

    List<PostResource> findAllByPostSeq(
            Long seq
    );
}
