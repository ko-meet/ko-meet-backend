package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.model.entities.QPostResource;
import com.backend.immilog.post.model.enums.ResourceType;
import com.backend.immilog.post.model.repositories.PostResourceRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostResourceRepositoryImpl implements PostResourceRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;


    @Override
    public void deleteAllEntities(
            Long postSeq,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        QPostResource postResource = QPostResource.postResource;
        BooleanExpression criteria = getCriteria(
                postSeq,
                deleteAttachments,
                resourceType,
                postResource
        );
        queryFactory.delete(postResource)
                .where(criteria)
                .execute();
    }

    @Override
    public void deleteAllByPostSeq(
            Long seq
    ) {
        QPostResource postResource = QPostResource.postResource;
        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, postResource);
        deleteClause
                .where(postResource.postSeq.eq(seq))
                .execute();
    }

    private static BooleanExpression getCriteria(
            Long postSeq,
            List<String> deleteAttachments,
            ResourceType resourceType,
            QPostResource postResource
    ) {
        return postResource.content.in(deleteAttachments)
                .and(postResource.postSeq.eq(postSeq)
                        .and(postResource.resourceType.eq(resourceType)));
    }
}
