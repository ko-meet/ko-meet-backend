package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.repositories.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@DisplayName("BookmarkCreationService 테스트")
class BookmarkCreationServiceTest {
    @Mock
    private BookmarkRepository bookmarkRepository;
    private BookmarkCreationService bookmarkCreationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookmarkCreationService = new BookmarkCreationService(bookmarkRepository);
    }

    @Test
    @DisplayName("북마크 생성 테스트")
    void createBookmarkTest() {
        // given
        Long userSeq = 1L;
        Long postSeq = 1L;
        String bookmarkTypeString = "post";

        // when
        bookmarkCreationService.createBookmark(userSeq, postSeq, bookmarkTypeString);

        // then
        verify(bookmarkRepository).saveEntity(Mockito.any());
    }


}