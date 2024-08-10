package com.backend.komeet.post.repositories;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.model.dtos.CommentDto;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.dtos.SearchResultDto;
import com.backend.komeet.post.model.entities.*;
import com.backend.komeet.user.enums.Countries;
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
import java.util.Optional;

import static com.backend.komeet.global.exception.ErrorCode.POST_NOT_FOUND;
import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.post.enums.SortingMethods.CREATED_DATE;

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

        Predicate predicate = predicateBuilder.getValue();

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getSize(post, predicate);

        List<PostDto> postDtos = convertIntoPostDtos(posts);

        return new PageImpl<>(postDtos, pageable, total);
    }

    private static List<PostDto> convertIntoPostDtos(
            List<Post> posts
    ) {
        return posts.stream()
                .map(PostDto::from)
                .toList();
    }

    private static List<Long> getPostIds(List<Post> posts) {
        return posts.stream()
                .map(Post::getSeq)
                .toList();
    }

    @Override
    public Page<SearchResultDto> searchPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        QPost post = QPost.post;

        Predicate predicate = post.postMetaData.content.contains(keyword)
                .or(post.postMetaData.title.contains(keyword))
                .or(post.postMetaData.tags.any().contains(keyword));

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(CREATED_DATE, post);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getSize(post, predicate);

        List<SearchResultDto> results = convertIntoSearchResultDto(keyword, posts);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<PostDto> getMyPosts(
            Long userId,
            Pageable pageable
    ) {
        QPost post = QPost.post;

        Predicate predicate = post.user.seq.eq(userId);
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(CREATED_DATE, post);

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = getSize(post, predicate);

        List<PostDto> postDtos = convertIntoPostDtos(posts);

        return new PageImpl<>(postDtos, pageable, total);
    }

    @Override
    public PostDto getPost(
            Long postSeq
    ) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QBookmark bookmark = QBookmark.bookmark;

        Post postTbl = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(post.seq.eq(postSeq))
                .fetchOne();

        if (postTbl == null) {
            throw new CustomException(POST_NOT_FOUND);
        }

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .leftJoin(comment.post).fetchJoin()
                .leftJoin(comment.replies).fetchJoin()
                .leftJoin(comment.user).fetchJoin()
                .where(comment.post.seq.eq(postTbl.getSeq()))
                .fetch();

        List<CommentDto> commentDtos = comments.stream()
                .distinct()
                .map(CommentDto::from)
                .toList();

        PostDto postDto = PostDto.from(postTbl);
        postDto.setComments(commentDtos);

        return postDto;
    }

    @Override
    public Optional<Post> getPostWithBookmarkList(
            Long postSeq
    ) {
        QPost post = QPost.post;
        QBookmark bookmark = QBookmark.bookmark;
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(CREATED_DATE, post);

        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(post)
                        .leftJoin(post.bookmarklist, bookmark).fetchJoin()
                        .where(post.seq.eq(postSeq))
                        .orderBy(orderSpecifier)
                        .fetchOne()
        );
    }

    private OrderSpecifier<?> getOrderSpecifier(
            SortingMethods sortingMethod,
            QPost post
    ) {
        return switch (sortingMethod) {
            case CREATED_DATE -> post.createdAt.desc();
            case VIEW_COUNT -> post.postMetaData.viewCount.desc();
            case LIKE_COUNT -> post.postMetaData.likeCount.desc();
            case COMMENT_COUNT -> post.comments.size().desc();
            default -> post.createdAt.desc();
        };
    }

    private int getSize(
            QPost post,
            Predicate predicate
    ) {
        return jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetch()
                .size();
    }

    private static List<SearchResultDto> convertIntoSearchResultDto(
            String keyword,
            List<Post> posts
    ) {
        return posts.stream()
                .map(i -> SearchResultDto.from(i, keyword))
                .toList();
    }

}
