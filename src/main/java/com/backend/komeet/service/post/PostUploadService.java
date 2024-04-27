package com.backend.komeet.service.post;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.PostDto;
import com.backend.komeet.dto.SearchResultDto;
import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.SortingMethods;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.exception.ErrorCode.POST_NOT_FOUND;
import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
     *
     * @param userId            사용자 식별자
     * @param postUploadRequest 게시물 생성 요청 데이터
     */
    @Transactional
    public void uploadPost(Long userId, PostUploadRequest postUploadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        postRepository.save(Post.from(postUploadRequest, user));
    }

    /**
     * 게시물을 조회하는 메서드
     *
     * @param country       국가
     * @param sortingMethod 정렬 방식
     * @param isPublic      공개 여부
     * @return 조회된 게시물
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getPosts(Countries country,
                                  SortingMethods sortingMethod,
                                  String isPublic,
                                  Categories category,
                                  Integer page) {

        Pageable pageable = PageRequest.of(page, 10);

        return postRepository.getPosts(
                country, sortingMethod, isPublic, category, pageable
        );
    }

    /**
     * 게시물 상세 조회 메서드
     *
     * @param postSeq 게시물 식별자
     * @return 조회된 게시물
     */
    @Transactional(readOnly = true)
    public PostDto getPost(Long postSeq) {
        return PostDto.from(
                postRepository.findById(postSeq).orElseThrow(
                        () -> new CustomException(POST_NOT_FOUND))
        );
    }

    /**
     * 게시물 검색 메서드
     *
     * @param keyword 검색어
     * @param page
     * @return 검색된 게시물
     */
    @Transactional(readOnly = true)
    public Page<SearchResultDto> searchKeyword(String keyword, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        return postRepository.searchPostsByKeyword(keyword, pageable);
    }

    /**
     * 사용자 게시물 목록 조회 메서드
     * @param userId 사용자 식별자
     * @param page 페이지 번호
     * @return 내 게시물 목록
     */
    @Transactional(readOnly = true)
    public Page<PostDto> getUserPosts(Long userId, Integer page) {
        Pageable pageable = PageRequest.of(page, 15);
        return postRepository.getMyPosts(userId, pageable);
    }
}
