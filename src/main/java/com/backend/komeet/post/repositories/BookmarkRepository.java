package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 북마크 관련 데이터베이스 처리
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserSeq(Long userSeq);
}
