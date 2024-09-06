package com.backend.immilog.post.application;

import com.backend.immilog.post.application.services.CommentUploadService;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.domain.repositories.PostRepository;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.immilog.post.exception.PostErrorCode.INVALID_REFERENCE_TYPE;
import static com.backend.immilog.post.exception.PostErrorCode.POST_NOT_FOUND;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("CommentUploadService 테스트")
class CommentUploadServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    private CommentUploadService commentUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentUploadService = new CommentUploadService(
                commentRepository,
                postRepository
        );
    }

    @Test
    @DisplayName("댓글 업로드 - 성공")
    void uploadComment() {
        // given
        Long userId = 1L;
        Long postSeq = 1L;
        String referenceType = "posts";
        CommentUploadRequest commentUploadRequest =
                new CommentUploadRequest("content");
        Post post = Post.builder().commentCount(0L).build();
        when(postRepository.getById(postSeq)).thenReturn(Optional.of(post));
        // when
        commentUploadService.uploadComment(
                userId,
                postSeq,
                referenceType,
                commentUploadRequest.content()
        );
        // then
        verify(postRepository, times(1)).getById(postSeq);
        verify(commentRepository, times(1)).saveEntity(any());
    }

    @Test
    @DisplayName("댓글 업로드 - 실패:없는 게시물")
    void uploadComment_fail() {
        // given
        Long userId = 1L;
        Long postSeq = 1L;
        String referenceType = "posts";
        CommentUploadRequest commentUploadRequest =
                new CommentUploadRequest("content");
        when(postRepository.getById(postSeq)).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> commentUploadService.uploadComment(
                userId,
                postSeq,
                referenceType,
                commentUploadRequest.content()
        ))
                .isInstanceOf(PostException.class)
                .hasMessage(POST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("댓글 업로드 - 실패:없는 참조 타입")
    void uploadComment_fail_invalid_ref_type() {
        // given
        Long userId = 1L;
        Long postSeq = 1L;
        String referenceType = "text";
        CommentUploadRequest commentUploadRequest =
                new CommentUploadRequest("content");
        Post post = Post.builder().commentCount(0L).build();
        when(postRepository.getById(postSeq)).thenReturn(Optional.of(post));
        // when & then
        assertThatThrownBy(() -> commentUploadService.uploadComment(
                userId,
                postSeq,
                referenceType,
                commentUploadRequest.content()
        ))
                .isInstanceOf(PostException.class)
                .hasMessage(INVALID_REFERENCE_TYPE.getMessage());
    }

}