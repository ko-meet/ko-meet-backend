package com.backend.immilog.user.infrastructure.repositories;

import com.backend.immilog.user.domain.model.Bookmark;
import com.backend.immilog.user.domain.repositories.BookmarkRepository;
import com.backend.immilog.user.infrastructure.jpa.entity.BookmarkEntity;
import com.backend.immilog.user.infrastructure.jpa.repositories.BookmarkJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepository {
    private final BookmarkJpaRepository bookmarkJpaRepository;

    @Override
    public void saveEntity(
            Bookmark bookmark
    ) {
        bookmarkJpaRepository.save(BookmarkEntity.from(bookmark));
    }
}
