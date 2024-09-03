package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.application.dtos.CommentDTO;

import java.util.List;

public interface CommentRepositoryCustom {
    List<CommentDTO> getComments(
            Long postSeq
    );
}
