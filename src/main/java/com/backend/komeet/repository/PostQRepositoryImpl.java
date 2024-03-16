package com.backend.komeet.repository;

import com.backend.komeet.domain.QPost;
import com.backend.komeet.dto.PostDto;
import com.backend.komeet.dto.SearchResultDto;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.SortingMethods;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 관련 Querydsl 레포지토리 구현체
 */
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

        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (!category.equals(Categories.ALL) || !country.equals(Countries.ALL)) {
            if (!category.equals(Categories.ALL)) {
                predicateBuilder.and(post.category.eq(category));
            }
            if (!country.equals(Countries.ALL)) {
                predicateBuilder.and(post.country.eq(country));
            }
        }

        predicateBuilder.and(post.isPublic.eq(isPublic));

        Predicate predicate = predicateBuilder.getValue();

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        // 전체 결과 개수 계산
        Long total = getLength(predicate);

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

    @Override
    public Page<SearchResultDto> searchPostsByKeyword(String keyword, Pageable pageable) {
        QPost post = QPost.post;

        // 검색 조건 설정
        Predicate predicate = post.content.contains(keyword)
                .or(post.title.contains(keyword))
                .or(post.tags.any().contains(keyword));

        // 전체 결과 개수 계산
        Long total = getLength(predicate);

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(SortingMethods.CREATED_DATE, post);

        List<SearchResultDto> results = jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(i -> SearchResultDto.from(i, keyword))
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<PostDto> getMyPosts(Long userId, Pageable pageable) {
        QPost post = QPost.post;
        Predicate predicate = post.user.seq.eq(userId);
        Long total = getLength(predicate);
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(SortingMethods.CREATED_DATE, post);

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

    private Long getLength(Predicate predicate) {
        QPost post = QPost.post;
        return jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetchCount();
    }
}
