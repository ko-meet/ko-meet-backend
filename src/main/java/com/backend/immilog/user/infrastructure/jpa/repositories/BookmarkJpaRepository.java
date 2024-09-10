package com.backend.immilog.user.infrastructure.jpa.repositories;

import com.backend.immilog.user.infrastructure.jpa.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<BookmarkEntity, Long> {
    Optional<BookmarkEntity> findByUserSeq(Long userSeq);
}
