package com.backend.komeet.service.notice;

import com.backend.komeet.notice.application.NoticeModifyService;
import com.backend.komeet.notice.presentation.request.NoticeModifyRequest;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.komeet.service.common.TestEntityGenerator.*;
import static com.backend.komeet.service.common.TestRequestGenerator.createNoticeModifyRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 수정 테스트")
class NoticeModifyServiceTest {

    private NoticeModifyService noticeModifyService;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeModifyService = new NoticeModifyService(
                userRepository, noticeRepository
        );
    }

    @Test
    @DisplayName("공지사항 수정 - 성공")
    void patchNotices_Success() {
        // given
        Long userSeq = 2L;
        NoticeModifyRequest request = createNoticeModifyRequest();
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(admin));
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        // when
        noticeModifyService.modifyNotice(userSeq, 1L, request);

        //then
        assertThat(notice.getContent()).isEqualTo(request.getContent());
        assertThat(notice.getTitle()).isEqualTo(request.getTitle());
    }

    @Test
    @DisplayName("공지사항 수정 - 실패(없는 공지)")
    void postNotices_Fail() {
        // given
        Long userSeq = 1L;
        NoticeModifyRequest request = createNoticeModifyRequest();
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(1L)).thenReturn(Optional.empty());

    }
}