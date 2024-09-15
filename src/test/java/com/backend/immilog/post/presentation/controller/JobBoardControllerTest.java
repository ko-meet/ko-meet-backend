package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.post.application.services.JobBoardUploadService;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import com.backend.immilog.post.presentation.request.JobBoardUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JobBoardController 테스트")
class JobBoardControllerTest {
    @Mock
    private JobBoardUploadService jobBoardUploadService;

    private JobBoardController jobBoardController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobBoardController = new JobBoardController(jobBoardUploadService);
    }

    @Test
    @DisplayName("구인구직 게시글 업로드 : 성공")
    void uploadJobBoard() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        Long userSeq = 1L;
        when(request.getAttribute("userSeq")).thenReturn(userSeq);
        JobBoardUploadRequest param = new JobBoardUploadRequest(
                1L,
                "title",
                "content",
                0L,
                0L,
                List.of("tag1", "tag2"),
                List.of("attachment1", "attachment2"),
                LocalDateTime.now(),
                Experience.JUNIOR,
                "salary",
                1L,
                PostStatus.NORMAL
        );
        // when
        ResponseEntity<PostApiResponse> result =
                jobBoardController.uploadJobBoard(request, param);
        // then
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
    }

}