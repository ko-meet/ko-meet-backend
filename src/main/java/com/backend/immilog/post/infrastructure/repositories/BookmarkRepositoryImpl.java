package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.Bookmark;
import com.backend.immilog.post.domain.repositories.BookmarkRepository;
import com.backend.immilog.post.infrastructure.jpa.entity.BookmarkEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.BookmarkJpaRepository;
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
