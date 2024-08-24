package com.backend.immilog.post.model.services;

import com.backend.immilog.post.presentation.request.PostUploadRequest;
import org.springframework.transaction.annotation.Transactional;

public interface PostUploadService {
    @Transactional
    void uploadPost(
            Long userSeq,
            PostUploadRequest postUploadRequest
    );
}
