package com.backend.komeet.repository;

import com.backend.komeet.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 게시판 레포지토리
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostQRepository  {
}
