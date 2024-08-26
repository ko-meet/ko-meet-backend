package com.backend.immilog.post.model.services;

import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import org.springframework.transaction.annotation.Transactional;

public interface CommentUploadService {

    @Transactional
    void uploadComment(
            Long userId,
            Long postSeq,
            String referenceType,
            CommentUploadRequest commentUploadRequest
    );
}
