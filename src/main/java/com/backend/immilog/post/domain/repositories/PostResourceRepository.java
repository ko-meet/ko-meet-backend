package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.enums.ResourceType;

import java.util.List;

public interface PostResourceRepository {
    void deleteAllEntities(
            Long postSeq,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostSeq(
            Long seq
    );
}
