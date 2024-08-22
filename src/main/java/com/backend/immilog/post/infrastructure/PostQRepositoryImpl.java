package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.post.model.entities.QInteractionUser;
import com.backend.immilog.post.model.entities.QPost;
import com.backend.immilog.post.model.entities.QPostResource;
import com.backend.immilog.user.enums.Countries;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.backend.immilog.post.enums.PostStatus.DELETED;
import static com.backend.immilog.post.enums.PostType.POST;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

@RequiredArgsConstructor
@Repository
public class PostQRepositoryImpl implements PostQRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostDTO> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        QPost post = QPost.post;
        QPostResource postResource = QPostResource.postResource;
        QInteractionUser interactionUser = QInteractionUser.interactionUser;

        Predicate predicate = generateCriteria(country, isPublic, category, post);
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        List<PostDTO> postDTOs = queryFactory
                .select(
                        post,
                        list(interactionUser),
                        list(postResource)
                )
                .from(post)
                .leftJoin(postResource).on(
                        postResource.postSeq.eq(post.seq)
                                .and(postResource.postType.eq(POST))
                )
                .leftJoin(interactionUser).on(
                        interactionUser.postSeq.eq(post.seq)
                                .and(interactionUser.postType.eq(POST))
                )
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(groupBy(post.seq).list(
                        Projections.constructor(
                                PostDTO.class,
                                post,
                                list(interactionUser),
                                list(postResource)
                        )
                ));

        long total = getSize(post, predicate);
        return new PageImpl<>(postDTOs, pageable, total);
    }

    private static Predicate generateCriteria(
            Countries country,
            String isPublic,
            Categories category,
            QPost post
    ) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();
        if (!category.equals(Categories.ALL)) {
            predicateBuilder.and(post.category.eq(category));
        }
        if (!country.equals(Countries.ALL)) {
            predicateBuilder.and(post.postMetaData.country.eq(country));
        }
        predicateBuilder
                .and(post.isPublic.eq(isPublic))
                .and(post.postMetaData.status.ne(DELETED));
        return predicateBuilder.getValue();
    }

    private OrderSpecifier<?> getOrderSpecifier(
            SortingMethods sortingMethod,
            QPost post
    ) {
        return switch (sortingMethod) {
            case CREATED_DATE -> post.createdAt.desc();
            case VIEW_COUNT -> post.postMetaData.viewCount.desc();
            case LIKE_COUNT -> post.postMetaData.likeCount.desc();
            default -> post.createdAt.desc();
        };
    }

    private int getSize(
            QPost post,
            Predicate predicate
    ) {
        return queryFactory.selectFrom(post)
                .where(predicate)
                .fetch()
                .size();
    }

}
