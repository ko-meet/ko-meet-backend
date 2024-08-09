package com.backend.komeet.service.post;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.application.post.PostDeleteService;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.komeet.global.exception.ErrorCode.ALREADY_DELETED_POST;
import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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

    User user = TestEntityGenerator.user;
    Post post = TestEntityGenerator.postNormal;
    Post postDeleted = TestEntityGenerator.postDeleted;

    @Test
    @DisplayName("성공")
    void deletePost() {
        // given
        when(postRepository.findById(post.getSeq())).thenReturn(Optional.of(post));
        when(userRepository.findById(user.getSeq())).thenReturn(Optional.of(user));
        // when
        postDeleteService.deletePost(user.getSeq(), post.getSeq());
        // then
        assertThat(post.getPostMetaData().getStatus()).isEqualTo(DELETED);
    }

    @Test
    @DisplayName("실패 - 이미 삭제된 게시물")
    void deletePost_fail() {
        // given
        when(postRepository.findById(post.getSeq())).thenReturn(Optional.of(postDeleted));
        when(userRepository.findById(user.getSeq())).thenReturn(Optional.of(user));
        // when & then
        assertThatThrownBy(() -> postDeleteService.deletePost(user.getSeq(), postDeleted.getSeq()))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ALREADY_DELETED_POST.getMessage());
    }
}