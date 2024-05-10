package com.backend.komeet.service.post;

import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.post.application.PostUpdateService;
import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
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

    User user = User.builder()
            .email("test@test.test")
            .password("test")
            .country(Countries.SOUTH_KOREA)
            .build();
    Post post = Post.builder()
            .seq(1L)
            .title("제목")
            .attachments(List.of("첨부파일1", "첨부파일2"))
            .tags(List.of("태그1", "태그2"))
            .isPublic("Y")
            .country(Countries.LAOS)
            .region("지역")
            .user(user)
            .content("내용")
            .comments(List.of())
            .build();

    @Test
    @DisplayName("성공")
    void updatePost() {
        // given
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title("새제목")
                .content("새내용")
                .isPublic(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        // when
        postUpdateService.updatePost(1L, 1L, postUpdateRequest);
        // then
        assertThat(post.getContent()).isEqualTo("새내용");
    }
}