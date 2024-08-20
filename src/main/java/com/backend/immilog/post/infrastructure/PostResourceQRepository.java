package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.enums.ResourceType;

import java.util.List;

public interface PostResourceQRepository {
    void deleteAllEntities(
            Long postSeq,
            ResourceType resourceType,
            List<String> deleteAttachments
    );
}
