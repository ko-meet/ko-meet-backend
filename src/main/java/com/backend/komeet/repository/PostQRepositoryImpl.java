package com.backend.komeet.repository;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.QPost;
import com.backend.komeet.dto.PostDto;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.SortingMethods;
import com.querydsl.core.Fetchable;
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
                                  Pageable pageable) {

        QPost post = QPost.post;

        Predicate predicate = country.equals(Countries.ALL)
                ? post.isPublic.eq(isPublic)
                : post.country.eq(country).and(post.isPublic.eq(isPublic));

        List<PostDto> results = jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .orderBy(post.createdAt.desc())
                .fetch()
                .stream()
                .map(PostDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, 0);
    }
}
