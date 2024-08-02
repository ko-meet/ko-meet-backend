package com.backend.komeet.post.repositories;

import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.model.dtos.CommentDto;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.dtos.SearchResultDto;
import com.backend.komeet.post.model.entities.Comment;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.entities.QComment;
import com.backend.komeet.post.model.entities.QPost;
import com.backend.komeet.user.enums.Countries;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.backend.komeet.post.enums.PostStatus.DELETED;

/**
 * 게시글 관련 Querydsl 레포지토리 구현체
 */
@RequiredArgsConstructor
@Repository
public class PostQRepositoryImpl implements PostQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostDto> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        if (!category.equals(Categories.ALL)) {
            predicateBuilder.and(post.category.eq(category));
        }
        if (!country.equals(Countries.ALL)) {
            predicateBuilder.and(post.country.eq(country));
        }

        predicateBuilder.and(post.isPublic.eq(isPublic)).and(post.status.ne(DELETED));
        Predicate predicate = predicateBuilder.getValue();

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        // 데이터와 전체 개수 한 번에 조회
        QueryResults<Post> results = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        long total = results.getTotal();
        List<Post> posts = results.getResults();

        // 댓글 조회를 Batch Fetching으로 최적화
        Map<Long, List<Comment>> commentsMap = jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.post).fetchJoin()
                .fetch()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                commentTbl -> commentTbl.getPost().getSeq()
                        )
                );

        // 결과 변환 및 정렬
        List<PostDto> postDtos = posts.stream()
                .map(postTbl -> {
                    PostDto postDto = PostDto.from(postTbl);
                    postDto.setComments(
                            commentsMap.getOrDefault(
                                            postTbl.getSeq(),
                                            Collections.emptyList()
                                    )
                                    .stream()
                                    .map(CommentDto::from)
                                    .collect(Collectors.toList())
                    );
                    return postDto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(postDtos, pageable, total);
    }


    @Override
    public Page<SearchResultDto> searchPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
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
    public Page<PostDto> getMyPosts(
            Long userId,
            Pageable pageable
    ) {
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

    private OrderSpecifier<?> getOrderSpecifier(
            SortingMethods sortingMethod,
            QPost post
    ) {
        return switch (sortingMethod) {
            case CREATED_DATE -> post.createdAt.desc();
            case VIEW_COUNT -> post.viewCount.desc();
            case LIKE_COUNT -> post.likeCount.desc();
            case COMMENT_COUNT -> post.comments.size().desc();
            default -> post.createdAt.desc();
        };
    }

    private Long getLength(
            Predicate predicate
    ) {
        QPost post = QPost.post;
        return jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetchCount();
    }
}
