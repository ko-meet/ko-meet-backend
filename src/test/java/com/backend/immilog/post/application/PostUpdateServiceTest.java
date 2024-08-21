package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.infrastructure.BulkInsertRepository;
import com.backend.immilog.post.enums.ResourceType;
import com.backend.immilog.post.infrastructure.PostRepository;
import com.backend.immilog.post.infrastructure.PostResourceRepository;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.entities.PostResource;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.backend.immilog.global.exception.ErrorCode.FAILED_TO_SAVE_POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("PostUpdateService 테스트")
class PostUpdateServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostResourceRepository postResourceRepository;
    @Mock
    private BulkInsertRepository bulkInsertRepository;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    private PostUpdateService postUpdateService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        postUpdateService = new PostUpdateService(
                postRepository,
                postResourceRepository,
                bulkInsertRepository
        );
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    @DisplayName("게시물 수정 - 성공")
    void updatePost() throws SQLException {
        // given
        Long userSeq = 1L;
        Long postSeq = 1L;
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .addAttachments(List.of("attachment"))
                .deleteAttachments(List.of("delete-attachment"))
                .addTags(List.of("tag"))
                .deleteTags(List.of("delete-tag"))
                .title("title")
                .content("content")
                .isPublic(true)
                .build();
        Post post = Post.builder()
                .userSeq(1L)
                .postMetaData(PostMetaData.builder().build())
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));
        doNothing().when(preparedStatement).setLong(eq(1), anyLong());
        doNothing().when(preparedStatement).setString(eq(2), anyString());
        doNothing().when(preparedStatement).setString(eq(3), anyString());
        doNothing().when(preparedStatement).setString(eq(4), anyString());

        ArgumentCaptor<BiConsumer<PreparedStatement, PostResource>> captor =
                ArgumentCaptor.forClass(BiConsumer.class);

        // when
        postUpdateService.updatePost(userSeq, postSeq, postUpdateRequest);

        // then
        verify(postResourceRepository, times(2))
                .deleteAllEntities(anyLong(), any(ResourceType.class), anyList());
        verify(bulkInsertRepository, times(2)).saveAll(
                anyList(),
                anyString(),
                captor.capture()
        );
    }

    @Disabled
    @Test
    @DisplayName("게시물 수정 - 실패")
    void updatePostFailed() throws SQLException {
        // given
        Long userSeq = 1L;
        Long postSeq = 1L;
        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .addAttachments(List.of("attachment"))
                .deleteAttachments(List.of("delete-attachment"))
                .addTags(List.of("tag"))
                .deleteTags(List.of("delete-tag"))
                .title("title")
                .content("content")
                .isPublic(true)
                .build();
        Post post = Post.builder()
                .userSeq(1L)
                .postMetaData(PostMetaData.builder().build())
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));

        doThrow(new SQLException("Mock SQL Exception"))
                .when(preparedStatement).setLong(anyInt(), anyLong());

        doAnswer(invocation -> {
            BiConsumer<PreparedStatement, PostResource> consumer = invocation.getArgument(2);
            PostResource postResource = PostResource.builder().postSeq(1L).build();

            consumer.accept(preparedStatement, postResource);

            return null;
        }).when(bulkInsertRepository).saveAll(anyList(), anyString(), any(BiConsumer.class));

        // when & then
        assertThatThrownBy(() ->
                postUpdateService.updatePost(userSeq, postSeq, postUpdateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessage(FAILED_TO_SAVE_POST.getMessage());

        verify(bulkInsertRepository).saveAll(anyList(), anyString(), any(BiConsumer.class));

    }

    @Test
    @DisplayName("게시물 조회수 증가")
    void increaseViewCount() {
        // given
        Long postSeq = 1L;
        Post post = Post.builder()
                .postMetaData(PostMetaData.builder().viewCount(0L).build())
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));

        // when
        postUpdateService.increaseViewCount(postSeq);

        // then
        assertThat(post.getPostMetaData().getViewCount()).isEqualTo(1L);
    }


}