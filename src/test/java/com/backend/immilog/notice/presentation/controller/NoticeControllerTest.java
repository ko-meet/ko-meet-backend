package com.backend.immilog.notice.presentation.controller;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.notice.model.enums.NoticeType;
import com.backend.immilog.notice.model.services.NoticeRegisterService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("공지사항 컨트롤러 테스트")
class NoticeControllerTest {
    @Mock
    private NoticeRegisterService noticeRegisterService;

    private NoticeController noticeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeController = new NoticeController(noticeRegisterService);
    }

    @Test
    @DisplayName("공지사항 등록 테스트")
    void registerNotice_success() {
        // given
        Long userSeq = 1L;
        String title = "제목";
        String content = "내용";
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRole userRole = UserRole.ROLE_ADMIN;
        when(request.getAttribute("userRole")).thenReturn(userRole);
        when(request.getAttribute("userSeq")).thenReturn(userSeq);
        NoticeRegisterRequest param = NoticeRegisterRequest.builder()
                .title(title)
                .content(content)
                .type(NoticeType.NOTICE)
                .build();


        // when
        ResponseEntity<ApiResponse> response = noticeController.registerNotice(param, request);

        // then
        verify(noticeRegisterService).registerNotice(userSeq, userRole, param);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);

    }
}