package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.model.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostQRepository {
}
