package com.backend.komeet.service.notice;

import com.backend.komeet.notice.application.NoticeInquiryService;
import com.backend.komeet.notice.model.dtos.NoticeDto;
import com.backend.komeet.notice.repositories.NoticeRepository;
import com.backend.komeet.user.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.backend.komeet.service.common.TestEntityGenerator.*;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 조회 테스트")
class NoticeInquiryServiceTest {

    private NoticeInquiryService noticeInquiryService;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeInquiryService = new NoticeInquiryService(
                noticeRepository, userRepository
        );
    }

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void getNotices_Success() {
        // given
        Long userSeq = 1L;
        Integer page = 0;
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(noticeRepository.getNotices(user, PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(NoticeDto.from(notice))));

        // when
        Page<NoticeDto> notices = noticeInquiryService.getNotices(userSeq, page);
        // then
        notices.forEach(noticeDto -> {
            Assertions.assertThat(noticeDto.getTitle()).isEqualTo(notice.getTitle());
            String content = notice.getContent();
            if (content.length() > 30) {
                content = content.substring(0, 30);
            }
            Assertions.assertThat(noticeDto.getContent()).isEqualTo(content + "...");
        });
    }

    @Test
    @DisplayName("안 앍은 공지사항 존재여부 체크 - 존재/미존재")
    void checkUnreadNotice_Success() {
        // given
        Long userSeq1 = 1L;
        Long userSeq2 = 2L;
        when(userRepository.findById(userSeq1)).thenReturn(Optional.of(user));
        when(userRepository.findById(userSeq2)).thenReturn(Optional.of(admin));
        when(noticeRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(
                user.getCountry(), user.getSeq()
        )).thenReturn(true);
        when(noticeRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(
                admin.getCountry(), admin.getSeq()
        )).thenReturn(true);

        // when
        boolean result1 = noticeInquiryService.isNoticeExist(userSeq1);
        boolean result2 = noticeInquiryService.isNoticeExist(userSeq2);

        // then
        Assertions.assertThat(result1).isTrue();
        Assertions.assertThat(result2).isTrue();

    }
}