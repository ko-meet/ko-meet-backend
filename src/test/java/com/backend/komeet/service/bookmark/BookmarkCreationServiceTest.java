package com.backend.komeet.service.bookmark;

import com.backend.komeet.post.application.bookmark.BookmarkCreationService;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.entities.Bookmark;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.repositories.BookmarkRepository;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.user.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.backend.komeet.post.enums.PostType.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

//TODO : BookmarkCreationServiceTest 테스트 코드 수정 필요
@Disabled
@DisplayName("북마크 생성/조회 서비스")
class BookmarkCreationServiceTest {
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private PostRepository postRepository;
    private BookmarkCreationService bookmarkCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookmarkCreationService = new BookmarkCreationService(
                bookmarkRepository, postRepository
        );
    }

    @Test
    @DisplayName("북마크 생성 - 성공")
    void createPostBookmark_Success() {
        // given
        User user = TestEntityGenerator.user;
        Post post = TestEntityGenerator.postNormal;
        Bookmark bookmark = TestEntityGenerator.bookmark;
        when(bookmarkRepository.findByUserSeq(user.getSeq()))
                .thenReturn(Optional.of(bookmark));
        when(postRepository.findById(post.getSeq()))
                .thenReturn(Optional.of(post));
        // when
        bookmarkCreationService.createPostBookmark(
                user.getSeq(),
                post.getSeq(),
                POST
        );
        // then
        verify(bookmarkRepository, times(1)).save(bookmark);
        assertThat(bookmark.getUserSeq()).isEqualTo(user.getSeq());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("북마크 조회 - 성공")
    void getBookmark_Success() {
        // given
        User user = TestEntityGenerator.user;
        Post post = TestEntityGenerator.postNormal;
        post.setCreatedAt(LocalDateTime.now());
        Bookmark bookmark = TestEntityGenerator.bookmark;
        when(bookmarkRepository.findByUserSeq(user.getSeq()))
                .thenReturn(Optional.of(bookmark));
        when(postRepository.findById(post.getSeq()))
                .thenReturn(Optional.of(post));
        // when
        List<?> bookmarkList =
                bookmarkCreationService.getBookmarkList(user.getSeq(), POST);
        // then
        assertThat(((List<PostDto>) bookmarkList).get(0).getSeq()).isEqualTo(post.getSeq());
    }

}
