package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Comment;

import java.util.Optional;

/**
 * 게시글 관련 Querydsl 레포지토리 인터페이스
 */
public interface CommentQRepository {
    Optional<Comment> getComment(
            Long commentSeq
    );
}
