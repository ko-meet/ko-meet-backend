package com.backend.immilog.post.application;

import com.backend.immilog.post.application.services.PostDeleteService;
import com.backend.immilog.post.domain.repositories.PostRepository;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.domain.vo.PostMetaData;
import com.backend.immilog.post.domain.vo.PostUserData;
import com.backend.immilog.post.domain.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.immilog.post.exception.PostErrorCode.NO_AUTHORITY;
import static com.backend.immilog.post.domain.model.enums.PostStatus.DELETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("PostDeleteService 테스트")
class PostDeleteServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostResourceRepository postResourceRepository;
    private PostDeleteService postDeleteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postDeleteService = new PostDeleteService(
                postRepository,
                postResourceRepository
        );
    }

    @Test
    @DisplayName("게시물 삭제 - 성공")
    void deletePost() {
        // given
        Long userId = 1L;
        Long postSeq = 1L;
        Post post = Post.builder()
                .seq(postSeq)
                .postMetaData(PostMetaData.builder().build())
                .postUserData(PostUserData.builder().userSeq(userId).build())
                .build();
        when(postRepository.getById(postSeq)).thenReturn(Optional.of(post));

        // when
        postDeleteService.deletePost(userId, postSeq);

        // then
        verify(postRepository).getById(postSeq);
        verify(postResourceRepository).deleteAllByPostSeq(postSeq);
        assertThat(post.postMetaData().getStatus()).isEqualTo(DELETED);
    }

    @Test
    @DisplayName("게시물 삭제 - 실패:권한없음")
    void deletePost_failed() {
        // given
        Long userId = 1L;
        Long postSeq = 1L;
        Post post = Post.builder()
                .seq(postSeq)
                .postMetaData(PostMetaData.builder().build())
                .postUserData(PostUserData.builder().userSeq(2L).build())
                .build();
        when(postRepository.getById(postSeq)).thenReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postDeleteService.deletePost(userId, postSeq))
                .isInstanceOf(PostException.class)
                .hasMessage(NO_AUTHORITY.getMessage());

    }
}