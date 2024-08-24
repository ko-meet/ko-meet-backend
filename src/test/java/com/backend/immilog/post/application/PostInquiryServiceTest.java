package com.backend.immilog.post.application;

import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.post.model.services.PostInquiryService;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.user.enums.Countries;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("PostInquiryService 테스트")
class PostInquiryServiceTest {
    @Mock
    private PostRepository postRepository;

    private PostInquiryService postInquiryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postInquiryService = new PostInquiryServiceImpl(postRepository);
    }

    @Test
    @DisplayName("게시물 조회 - 성공")
    void getPosts() {
        // given
        Countries country = Countries.SOUTH_KOREA;
        SortingMethods sortingMethod = SortingMethods.CREATED_DATE;
        String isPublic = "Y";
        Categories category = Categories.ALL;
        int page = 0;
        Pageable pageable = PageRequest.of(page, 10);
        PostDTO postDTO = mock(PostDTO.class);
        Page<PostDTO> posts = new PageImpl<>(List.of(postDTO));
        when(postRepository.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        )).thenReturn(posts);
        // when
        Page<PostDTO> result = postInquiryService.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                page
        );
        // then
        Assertions.assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시물 조회(단일 게시물)")
    void getPost() {
        // given
        Long postSeq = 1L;
        PostDTO postDTO = mock(PostDTO.class);
        when(postRepository.getPost(postSeq)).thenReturn(java.util.Optional.of(postDTO));
        // when
        PostDTO result = postInquiryService.getPost(postSeq);
        // then
        Assertions.assertThat(result).isEqualTo(postDTO);
    }
}