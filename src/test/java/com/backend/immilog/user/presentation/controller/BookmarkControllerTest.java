package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.user.application.services.BookmarkCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

@DisplayName("BookmarkController 테스트")
class BookmarkControllerTest {
    @Mock
    private BookmarkCreationService bookmarkCreationService;
    private BookmarkController bookmarkController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookmarkController = new BookmarkController(bookmarkCreationService);
    }

    @Test
    @DisplayName("북마크 등록 테스트")
    void createCommentTest() {
        // given
        Long postSeq = 1L;
        String postType = "post";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userSeq")).thenReturn(1L);
        // when
        bookmarkController.createComment(postSeq, postType, request);
        // then
        verify(bookmarkCreationService).createBookmark(any(), any(), any());
    }
}