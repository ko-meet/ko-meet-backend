package com.backend.komeet.post.application.post;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.dtos.SearchResultDto;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.presentation.request.PostUploadRequest;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 게시물 업로드 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class PostUploadService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시물을 생성하는 메서드
     */
    @Transactional
    public void uploadPost(
            Long userId,
            PostUploadRequest postUploadRequest
    ) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        postRepository.save(Post.from(postUploadRequest, user));
    }

    /**
     * 게시물을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Integer page
    ) {

        Pageable pageable = PageRequest.of(page, 10);

        return postRepository.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
    }

    /**
     * 게시물 상세 조회 메서드
     */
    @Transactional(readOnly = true)
    public PostDto getPost(
            Long postSeq
    ) {
        return postRepository.getPost(postSeq);
    }

    /**
     * 게시물 검색 메서드
     */
    @Transactional(readOnly = true)
    public Page<SearchResultDto> searchKeyword(
            String keyword,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 15);
        return postRepository.searchPostsByKeyword(keyword, pageable);
    }

    /**
     * 사용자 게시물 목록 조회 메서드
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getUserPosts(
            Long userId,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 15);
        return postRepository.getMyPosts(userId, pageable);
    }
}
