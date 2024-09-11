package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.infrastructure.jpa.entities.CommentEntity;
import com.backend.immilog.post.infrastructure.jpa.entities.QCommentEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.CommentJpaRepository;
import com.backend.immilog.post.infrastructure.result.CommentEntityResult;
import com.backend.immilog.user.infrastructure.jpa.entity.QUserEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.backend.immilog.post.domain.model.enums.ReferenceType.COMMENT;
import static com.backend.immilog.post.domain.model.enums.ReferenceType.POST;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final JPAQueryFactory queryFactory;
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public List<CommentResult> getComments(Long postSeq) {
        QCommentEntity comment = QCommentEntity.commentEntity;
        QCommentEntity childComment = new QCommentEntity("childComment");
        QUserEntity user = QUserEntity.userEntity;
        QUserEntity childCommentUser = new QUserEntity("childCommentUser");

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
                                        CommentEntityResult.class,
                                        comment,
                                        user,
                                        Projections.list(childComment),
                                        Projections.list(childCommentUser)
                                )
                        )
                )
                .stream()
                .map(CommentEntityResult::toCommentResult)
                .toList();
    }

    @Override
    public void saveEntity(Comment comment) {
        commentJpaRepository.save(CommentEntity.from(comment));
    }
}


