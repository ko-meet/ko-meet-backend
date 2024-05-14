package com.backend.komeet.service.post;

import com.backend.komeet.post.application.PostUploadService;
import com.backend.komeet.post.presentation.request.PostUploadRequest;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.service.common.TestRequestGenerator;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

@DisplayName("게시물 업로드 서비스 테스트")
class PostUploadServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    private PostUploadService postUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        postUploadService = new PostUploadService(postRepository, userRepository);
    }

    User user = TestEntityGenerator.user;

    PostUploadRequest postUploadRequest =
            TestRequestGenerator.createPostUploadRequest();

    @Test
    @DisplayName("성공")
    void createPost() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        postUploadService.uploadPost(1L, postUploadRequest);
        // then
        verify(postRepository, times(1)).save(Mockito.any());
    }
}