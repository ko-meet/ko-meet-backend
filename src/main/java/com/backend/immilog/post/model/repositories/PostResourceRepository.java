package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.model.entities.PostResource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostResourceRepository
        extends JpaRepository<PostResource, Long>, PostResourceRepositoryCustom {
}
