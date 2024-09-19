package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("NoticeRepository 테스트")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoticeRepositoryImplTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Test
    @DisplayName("공지사항 조회 - 성공")
    void getNotices_returnsPagedNotices() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Long userSeq = 1L;

        var result = noticeRepository.getNotices(userSeq, pageable);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("공지사항 조회 - 공지사항이 없는 경우")
    void getNotices_returnsEmptyPageWhenNoNotices() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Long userSeq = 999L;

        var result = noticeRepository.getNotices(userSeq, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("공지사항 저장 - 성공")
    void saveEntity_savesNoticeSuccessfully() {
        Notice notice = Notice.builder()
                .seq(1L)
                .userSeq(2L)
                .title("Title")
                .content("Content")
                .type(NoticeType.NOTICE)
                .status(NoticeStatus.NORMAL)
                .targetCountries(List.of(NoticeCountry.MALAYSIA))
                .readUsers(List.of(3L))
                .build();

        noticeRepository.saveEntity(notice);

        Optional<Notice> savedNotice = noticeRepository.findBySeq(1L);
        assertThat(savedNotice).isPresent();
        assertThat(savedNotice.get().title()).isEqualTo("Title");
    }

    @Test
    @DisplayName("공지사항 존재유무 체크 - 성공")
    void findBySeq_returnsNoticeWhenExists() {
        Long noticeSeq = 1L;

        Optional<Notice> result = noticeRepository.findBySeq(noticeSeq);

        assertThat(result).isPresent();
        assertThat(result.get().seq()).isEqualTo(noticeSeq);
    }

    @Test
    @DisplayName("공지사항 조회 - 공지사항이 없는 경우")
    void findBySeq_returnsEmptyWhenNotExists() {
        Long noticeSeq = 999L;

        Optional<Notice> result = noticeRepository.findBySeq(noticeSeq);

        assertThat(result).isNotPresent();
    }
}