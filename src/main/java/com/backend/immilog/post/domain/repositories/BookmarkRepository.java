package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.Bookmark;

public interface BookmarkRepository {
    void saveEntity(
            Bookmark bookmark
    );
}
