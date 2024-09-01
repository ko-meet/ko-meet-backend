package com.backend.immilog.notice.application;

import com.backend.immilog.notice.model.dtos.NoticeDTO;
import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.enums.NoticeType;
import com.backend.immilog.notice.model.repositories.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.backend.immilog.notice.model.enums.Countries.SOUTH_KOREA;
import static com.backend.immilog.notice.model.enums.NoticeStatus.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("공지사항 조회 테스트")
class NoticeInquiryServiceTest {
    @Mock
    private NoticeRepository noticeRepository;

    private NoticeInquiryService noticeInquiryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeInquiryService = new NoticeInquiryService(
                noticeRepository
        );
    }

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void getNotices_Success() {
        // given
        Long userSeq = 1L;
        int page = 0;
        Notice notice = Notice.builder()
                .title("title")
                .userSeq(userSeq)
                .content("content")
                .type(NoticeType.NOTICE)
                .targetCountries(List.of(SOUTH_KOREA))
                .status(NORMAL)
                .build();

        when(noticeRepository.getNotices(userSeq, PageRequest.of(page, 10)))
                .thenReturn(new PageImpl<>(List.of(NoticeDTO.from(notice))));
        // when
        Page<NoticeDTO> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices.get().findFirst().get().content()).isEqualTo("content");
    }

    @Test
    @DisplayName("공지사항 조회 - 실패")
    void getNotices_Fail() {
        // given
        Long userSeq = null;
        int page = 0;

        // when
        Page<NoticeDTO> notices = noticeInquiryService.getNotices(userSeq, page);

        // then
        assertThat(notices).isEmpty();
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 성공")
    void getNoticeDetail_Success() {
        // given
        Long noticeSeq = 1L;
        Notice notice = Notice.builder()
                .seq(noticeSeq)
                .title("title")
                .userSeq(1L)
                .content("content")
                .type(NoticeType.NOTICE)
                .targetCountries(List.of(SOUTH_KOREA))
                .status(NORMAL)
                .build();

        when(noticeRepository.findById(noticeSeq))
                .thenReturn(java.util.Optional.of(notice));
        // when
        NoticeDTO noticeDTO = noticeInquiryService.getNoticeDetail(noticeSeq);

        // then
        assertThat(noticeDTO.content()).isEqualTo("content");
    }

}