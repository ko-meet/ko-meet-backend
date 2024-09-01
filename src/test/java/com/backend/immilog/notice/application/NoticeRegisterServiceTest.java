package com.backend.immilog.notice.application;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.model.enums.NoticeType;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import com.backend.immilog.notice.model.services.NoticeRegisterService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("NoticeRegisterService 테스트")
class NoticeRegisterServiceTest {
    @Mock
    private NoticeRepository noticeRepository;
    private NoticeRegisterService noticeRegisterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeRegisterService = new NoticeRegisterServiceImpl(
                noticeRepository
        );
    }

    @Test
    @DisplayName("공지사항 등록 - 성공")
    void registerNotice() {
        // given
        Long userSeq = 1L;
        String title = "제목";
        String content = "내용";
        UserRole userRole = UserRole.ROLE_ADMIN;
        NoticeRegisterRequest param = NoticeRegisterRequest.builder()
                .title(title)
                .content(content)
                .type(NoticeType.NOTICE)
                .build();
        // when
        noticeRegisterService.registerNotice(userSeq, userRole, param);
        // then
        verify(noticeRepository, times(1)).save(any());
    }
}