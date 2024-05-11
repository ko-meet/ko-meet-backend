package com.backend.komeet.service.post;

import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.application.PostDeleteService;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.post.enums.PostStatus.NORMAL;
import static com.backend.komeet.infrastructure.exception.ErrorCode.ALREADY_DELETED_POST;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("게시물 삭제 관련 서비스 테스트")
class PostDeleteServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private PostDeleteService postDeleteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        postDeleteService = new PostDeleteService(postRepository, userRepository);
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
            .status(NORMAL)
            .build();

    @Test
    @DisplayName("성공")
    void deletePost() {
        // given
        when(postRepository.findById(post.getSeq())).thenReturn(Optional.of(post));
        when(userRepository.findById(user.getSeq())).thenReturn(Optional.of(user));
        // when
        postDeleteService.deletePost(user.getSeq(), post.getSeq());
        // then
        assertThat(post.getStatus()).isEqualTo(DELETED);
    }

    @Test
    @DisplayName("실패 - 이미 삭제된 게시물")
    void deletePost_fail() {
        // given
        post.setStatus(DELETED);
        when(postRepository.findById(post.getSeq())).thenReturn(Optional.of(post));
        when(userRepository.findById(user.getSeq())).thenReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> postDeleteService.deletePost(user.getSeq(), post.getSeq()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ALREADY_DELETED_POST.getMessage());
    }
}