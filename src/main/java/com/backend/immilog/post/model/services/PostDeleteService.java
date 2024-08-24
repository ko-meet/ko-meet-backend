package com.backend.immilog.post.model.services;

import org.springframework.transaction.annotation.Transactional;

public interface PostDeleteService {
    @Transactional
    void deletePost(
            Long userId,
            Long postSeq
    );
}
