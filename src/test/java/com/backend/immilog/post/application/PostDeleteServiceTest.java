package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.model.embeddables.PostUserData;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.repositories.PostResourceRepository;
import com.backend.immilog.post.model.services.PostDeleteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.immilog.post.enums.PostStatus.DELETED;
import static com.backend.immilog.post.exception.PostErrorCode.NO_AUTHORITY;
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
        postDeleteService = new PostDeleteServiceImpl(
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
                .postMetaData(
                        PostMetaData.builder().build()
                )
                .postUserData(
                        PostUserData.builder().userSeq(userId).build()
                )
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));

        // when
        postDeleteService.deletePost(userId, postSeq);

        // then
        verify(postRepository).findById(postSeq);
        verify(postResourceRepository).deleteAllByPostSeq(postSeq);
        assertThat(post.getPostMetaData().getStatus()).isEqualTo(DELETED);
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
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postDeleteService.deletePost(userId, postSeq))
                .isInstanceOf(CustomException.class)
                .hasMessage(NO_AUTHORITY.getMessage());

    }
}