package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.dtos.SearchResultDto;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.enums.SortingMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 게시글 관련 Querydsl 레포지토리 인터페이스
 */
public interface PostQRepository {
    Page<PostDto> getPosts(Countries country,
                           SortingMethods sortingMethod,
                           String isPublic,
                           Categories category,
                           Pageable pageable);

    Page<SearchResultDto> searchPostsByKeyword(String keyword, Pageable pageable);

    Page<PostDto> getMyPosts(Long userId, Pageable pageable);
}
