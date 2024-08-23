package com.backend.immilog.post.model.services;

import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface PostUpdateService {
    @Transactional
    void updatePost(
            Long userId,
            Long postSeq,
            PostUpdateRequest postUpdateRequest
    );

    @Async
    @Transactional
    void increaseViewCount(Long postSeq);

    @Async
    @Transactional
    void likePost(
            Long userSeq,
            Long postSeq
    );
}
