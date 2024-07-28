package com.backend.komeet.service.notice;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.notice.application.NoticeRegisterService;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.notice.presentation.request.NoticeRegisterRequest;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.komeet.global.exception.ErrorCode.NOT_AN_ADMIN_USER;
import static com.backend.komeet.service.common.TestEntityGenerator.*;
import static com.backend.komeet.service.common.TestRequestGenerator.createNoticeRegisterRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 조회 테스트")
class NoticeRegisterServiceTest {

    private NoticeRegisterService noticeRegisterService;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeRegisterService = new NoticeRegisterService(
                userRepository, noticeRepository
        );
    }
    @Test
    @DisplayName("공지사항 등록 - 성공")
    void postNotices_Success() {
        // given
        Long userSeq = 2L;
        NoticeRegisterRequest request = createNoticeRegisterRequest();
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(admin));

        // when
        noticeRegisterService.registerNotice(userSeq, request);

        //then
        verify(noticeRepository).save(Mockito.any(Notice.class));

    }
    @Test
    @DisplayName("공지사항 등록 - 실패(일반유저)")
    void postNotices_Fail() {
        // given
        Long userSeq = 1L;
        NoticeRegisterRequest request = createNoticeRegisterRequest();
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> noticeRegisterService.registerNotice(userSeq, request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", NOT_AN_ADMIN_USER);
    }
}