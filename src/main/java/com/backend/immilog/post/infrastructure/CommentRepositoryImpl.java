package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.application.dtos.CommentDTO;
import com.backend.immilog.post.model.entities.QComment;
import com.backend.immilog.post.model.repositories.CommentRepositoryCustom;
import com.backend.immilog.user.model.entities.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.backend.immilog.post.model.enums.ReferenceType.COMMENT;
import static com.backend.immilog.post.model.enums.ReferenceType.POST;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDTO> getComments(Long postSeq) {
        QComment comment = QComment.comment;
        QComment childComment = new QComment("childComment");
        QUser user = QUser.user;
        QUser childCommentUser = new QUser("childCommentUser");

        return queryFactory
                .select(comment, user, list(childComment), list(childCommentUser))
                .from(comment)
                .leftJoin(user)
                .on(comment.userSeq.eq(user.seq))
                .leftJoin(childComment)
                .on(childComment.postSeq.eq(postSeq)
                        .and(childComment.parentSeq.eq(comment.seq))
                        .and(childComment.referenceType.eq(COMMENT))
                )
                .leftJoin(childCommentUser)
                .on(childComment.userSeq.eq(childCommentUser.seq))
                .where(comment.postSeq.eq(postSeq)
                        .and(comment.parentSeq.isNull())
                        .and(comment.referenceType.eq(POST))
                )
                .orderBy(comment.createdAt.desc())
                .transform(
                        groupBy(comment.postSeq).list(
                                Projections.constructor(
                                        CommentDTO.class,
                                        comment,
                                        user,
                                        Projections.list(childComment),
                                        Projections.list(childCommentUser)
                                )
                        )
                );
    }
}


