package com.backend.komeet.repository;

import com.backend.komeet.domain.QPost;
import com.backend.komeet.dto.PostDto;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.SortingMethods;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PostQRepositoryImpl implements PostQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostDto> getPosts(Countries country,
                                  SortingMethods sortingMethod,
                                  String isPublic,
                                  Categories category,
                                  Pageable pageable) {

        QPost post = QPost.post;

        Predicate predicate = null;

        if(category.equals(Categories.ALL) && country.equals(Countries.ALL)) {
            predicate = post.isPublic.eq(isPublic);
        }else if(category.equals(Categories.ALL)) {
            predicate = post.country.eq(country).and(post.isPublic.eq(isPublic));
        }else if(country.equals(Countries.ALL)) {
            predicate = post.category.eq(category).and(post.isPublic.eq(isPublic));
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        // 전체 결과 개수 계산
        long total = jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetchCount();

        // 데이터 조회 및 정렬
        List<PostDto> results = jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(PostDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifier(SortingMethods sortingMethod, QPost post) {
        switch (sortingMethod) {
            case CREATED_DATE:
                return post.createdAt.desc();
            case VIEW_COUNT:
                return post.viewCount.desc();
            case LIKE_COUNT:
                return post.likeCount.desc();
            case COMMENT_COUNT:
                return post.comments.size().desc();
            default:
                return post.createdAt.desc();
        }
    }

}