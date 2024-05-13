package com.backend.komeet.service.post;

import com.backend.komeet.post.application.PostUpdateService;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.service.common.TestRequestGenerator;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("게시물 수정 서비스 테스트")
class PostUpdateServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private PostUpdateService postUpdateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        postUpdateService = new PostUpdateService(postRepository, userRepository);
    }

    User user = TestEntityGenerator.user;
    Post post = TestEntityGenerator.postNormal;
    PostUpdateRequest postUpdateRequest =
            TestRequestGenerator.createPostUpdateRequest();

    @Test
    @DisplayName("성공")
    void updatePost() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        // when
        postUpdateService.updatePost(1L, 1L, postUpdateRequest);
        // then
        assertThat(post.getContent()).isEqualTo("새내용");
    }
}