package com.backend.immilog.user.domain.repositories;

import com.backend.immilog.user.domain.model.Bookmark;

public interface BookmarkRepository {
    void saveEntity(
            Bookmark bookmark
    );
}
