package com.backend.immilog.notice.application;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.notice.application.services.NoticeCreateService;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
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
class NoticeCreateServiceTest {
    @Mock
    private NoticeRepository noticeRepository;
    private NoticeCreateService noticeRegisterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeRegisterService = new NoticeCreateService(
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
        String userRole = UserRole.ROLE_ADMIN.name();
        NoticeRegisterRequest param = NoticeRegisterRequest.builder()
                .title(title)
                .content(content)
                .type(NoticeType.NOTICE)
                .build();
        // when
        noticeRegisterService.registerNotice(userSeq, userRole, param.toCommand());
        // then
        verify(noticeRepository, times(1)).saveEntity(any());
    }
}