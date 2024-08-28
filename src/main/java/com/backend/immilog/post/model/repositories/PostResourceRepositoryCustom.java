package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.model.enums.ResourceType;

import java.util.List;

public interface PostResourceRepositoryCustom {
    void deleteAllEntities(
            Long postSeq,
            ResourceType resourceType,
            List<String> deleteAttachments
    );

    void deleteAllByPostSeq(
            Long seq
    );
}
