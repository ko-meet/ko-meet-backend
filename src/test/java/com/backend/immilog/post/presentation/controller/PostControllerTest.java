package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.post.application.PostDeleteService;
import com.backend.immilog.post.application.PostUpdateService;
import com.backend.immilog.post.application.PostUploadService;
import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@DisplayName("PostController 테스트")
class PostControllerTest {
    @Mock
    private PostUploadService postUploadService;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PostUpdateService postUpdateService;
    @Mock
    private PostDeleteService postDeleteService;
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(
                postUploadService,
                postUpdateService,
                postDeleteService,
                jwtProvider
        );
    }

    @Test
    @DisplayName("게시물 작성")
    void createPost() {
        // given
        String token = "token";
        PostUploadRequest postUploadRequest = PostUploadRequest.builder()
                .category(Categories.COMMUNICATION)
                .title("title")
                .content("content")
                .isPublic(true)
                .build();
        Location location = Location.builder()
                .country(Countries.SOUTH_KOREA)
                .region("region")
                .build();
        User user = User.builder()
                .seq(1L)
                .location(location)
                .build();

        when(jwtProvider.getIdFromToken(token)).thenReturn(user.getSeq());

        // when
        ResponseEntity<ApiResponse> response = postController.createPost(
                token,
                postUploadRequest
        );

        // then
        verify(postUploadService).uploadPost(user.getSeq(), postUploadRequest);
        assertThat(response.getStatusCode()).isEqualTo(ResponseEntity.status(CREATED).build().getStatusCode());
    }

    @Test
    @DisplayName("게시물 수정")
    void updatePost() {
        // given
        Long postSeq = 1L;
        String token = "token";
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .addAttachments(List.of("attachment"))
                .deleteAttachments(List.of("delete-attachment"))
                .addTags(List.of("tag"))
                .deleteTags(List.of("delete-tag"))
                .title("title")
                .content("content")
                .isPublic(true)
                .build();
        when(jwtProvider.getIdFromToken(token)).thenReturn(1L);

        // when
        ResponseEntity<ApiResponse> response = postController.updatePost(
                postSeq,
                token,
                postUpdateRequest
        );

        // then
        verify(postUpdateService).updatePost(1L, postSeq, postUpdateRequest);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("게시물 삭제")
    void deletePost() {
        // given
        Long postSeq = 1L;
        String token = "token";
        when(jwtProvider.getIdFromToken(token)).thenReturn(1L);

        // when
        ResponseEntity<Void> response = postController.deletePost(
                postSeq,
                token
        );

        //then
        verify(postDeleteService).deletePost(1L, postSeq);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }
}