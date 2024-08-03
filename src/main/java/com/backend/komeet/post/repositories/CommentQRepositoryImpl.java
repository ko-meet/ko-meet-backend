package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Comment;
import com.backend.komeet.post.model.entities.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 댓글 관련 Querydsl 레포지토리 구현체
 */
@RequiredArgsConstructor
@Repository
public class CommentQRepositoryImpl implements CommentQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Comment> getComment(Long commentSeq) {
        QComment comment = QComment.comment;
        Comment commentTbl = jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.post).fetchJoin()
                .leftJoin(comment.replies).fetchJoin()
                .leftJoin(comment.user).fetchJoin()
                .where(comment.seq.eq(commentSeq))
                .fetchOne();
        return commentTbl == null ? Optional.empty() : Optional.of(commentTbl);
    }
}
