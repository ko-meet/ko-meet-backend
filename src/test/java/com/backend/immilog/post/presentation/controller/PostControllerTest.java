package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.post.application.PostUploadService;
import com.backend.immilog.post.enums.Categories;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("PostController 테스트")
class PostControllerTest {
    @Mock
    private PostUploadService postUploadService;
    @Mock
    private JwtProvider jwtProvider;
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(
                postUploadService,
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
}