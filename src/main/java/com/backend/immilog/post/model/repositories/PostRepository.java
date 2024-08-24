package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
}
