package com.backend.komeet.service.post;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.PostCreateRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
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

    User user = User.builder()
            .email("test@test.test")
            .password("test")
            .country(Countries.SOUTH_KOREA)
            .build();

    @Test
    @DisplayName("성공")
    void createPost() {
        // given
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title("제목")
                .content("내용")
                .attachments(List.of("test"))
                .tags(List.of("test"))
                .isPublic(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        postUploadService.createPost(1L, postCreateRequest);
        // then
        verify(postRepository, times(1)).save(Mockito.any());
    }
}