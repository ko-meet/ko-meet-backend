package com.backend.immilog.post.application;

import com.backend.immilog.global.application.RedisDistributedLock;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.model.embeddables.PostUserData;
import com.backend.immilog.post.model.entities.InteractionUser;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.entities.PostResource;
import com.backend.immilog.post.model.enums.ResourceType;
import com.backend.immilog.post.model.repositories.BulkInsertRepository;
import com.backend.immilog.post.model.repositories.InteractionUserRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.repositories.PostResourceRepository;
import com.backend.immilog.post.model.services.PostUpdateService;
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

import static com.backend.immilog.post.exception.PostErrorCode.FAILED_TO_SAVE_POST;
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
    private InteractionUserRepository interactionUserRepository;

    @Mock
    private RedisDistributedLock redisDistributedLock;
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
        postUpdateService = new PostUpdateServiceImpl(
                postRepository,
                postResourceRepository,
                bulkInsertRepository,
                interactionUserRepository,
                redisDistributedLock
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
                .postUserData(PostUserData.builder().userSeq(1L).build())
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
                .postUserData(PostUserData.builder().userSeq(1L).build())
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
                .isInstanceOf(PostException.class)
                .hasMessage(FAILED_TO_SAVE_POST.getMessage());

        verify(bulkInsertRepository).saveAll(anyList(), anyString(), any(BiConsumer.class));

    }

    @Test
    @DisplayName("게시물 조회수 증가 - 성공")
    void increaseViewCount() {
        // given
        Long postSeq = 1L;
        Post post = Post.builder()
                .postMetaData(PostMetaData.builder().viewCount(0L).build())
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));
        when(redisDistributedLock.tryAcquireLock(anyString(), anyString()))
                .thenReturn(true);
        // when
        postUpdateService.increaseViewCount(postSeq);

        // then
        assertThat(post.getPostMetaData().getViewCount()).isEqualTo(1L);
        verify(redisDistributedLock).releaseLock(anyString(), anyString());
    }

    @Test
    @DisplayName("게시물 조회수 증가 - 실패")
    void increaseViewCount_fail() {
        // given
        Long postSeq = 1L;
        Post post = Post.builder()
                .postMetaData(PostMetaData.builder().viewCount(0L).build())
                .build();
        when(postRepository.findById(postSeq)).thenReturn(Optional.of(post));
        when(redisDistributedLock.tryAcquireLock(anyString(), anyString()))
                .thenReturn(false, false, false);
        // when
        postUpdateService.increaseViewCount(postSeq);

        // then
        assertThat(post.getPostMetaData().getViewCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("게시물 좋아요 - 성공:이미 좋아요 한 유저")
    void likePost_success_exits() {
        // given
        Long postSeq = 1L;
        Long userSeq = 2L;
        InteractionUser likeUser = InteractionUser.builder()
                .userSeq(2L)
                .postSeq(postSeq)
                .build();
        when(interactionUserRepository.findByPostSeq(postSeq))
                .thenReturn(List.of(likeUser));
        when(redisDistributedLock.tryAcquireLock(anyString(), anyString()))
                .thenReturn(true);
        // when
        postUpdateService.likePost(userSeq, postSeq);
        // then
        verify(interactionUserRepository, times(1))
                .delete(any(InteractionUser.class));
    }

    @Test
    @DisplayName("게시물 좋아요 - 성공:새로 좋아요 추가")
    void likePost_success_new() {
        // given
        Long postSeq = 1L;
        Long userSeq = 2L;
        InteractionUser likeUser = InteractionUser.builder()
                .userSeq(3L)
                .postSeq(postSeq)
                .build();
        when(interactionUserRepository.findByPostSeq(postSeq))
                .thenReturn(List.of(likeUser));
        when(redisDistributedLock.tryAcquireLock(anyString(), anyString()))
                .thenReturn(true);
        // when
        postUpdateService.likePost(userSeq, postSeq);
        // then
        verify(interactionUserRepository, times(1))
                .save(any(InteractionUser.class));
    }


}