package com.backend.immilog.notice.infrastructure.jpa.entities;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import com.backend.immilog.notice.infrastructure.jpa.NoticeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NoticeEntity 테스트")
class NoticeEntityTest {

    @Test
    @DisplayName("NoticeEntity 생성 테스트")
    void from_createsNoticeEntityFromNotice() {
        Notice notice = Notice.builder()
                .seq(1L)
                .userSeq(2L)
                .title("Title")
                .content("Content")
                .type(NoticeType.NOTICE)
                .status(NoticeStatus.NORMAL)
                .targetCountries(List.of(NoticeCountry.SINGAPORE))
                .readUsers(List.of(3L))
                .build();

        NoticeEntity noticeEntity = NoticeEntity.from(notice);

        assertThat(noticeEntity.getSeq()).isEqualTo(notice.seq());
        assertThat(noticeEntity.getUserSeq()).isEqualTo(notice.userSeq());
        assertThat(noticeEntity.getTitle()).isEqualTo(notice.title());
        assertThat(noticeEntity.getContent()).isEqualTo(notice.content());
        assertThat(noticeEntity.getType()).isEqualTo(notice.type());
        assertThat(noticeEntity.getStatus()).isEqualTo(notice.status());
        assertThat(noticeEntity.getTargetCountries()).isEqualTo(notice.targetCountries());
        assertThat(noticeEntity.getReadUsers()).isEqualTo(notice.readUsers());
    }

    @Test
    @DisplayName("NoticeEntity toDomain 메서드 테스트")
    void toDomain_createsNoticeFromNoticeEntity() {
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .seq(1L)
                .userSeq(2L)
                .title("Title")
                .content("Content")
                .type(NoticeType.NOTICE)
                .status(NoticeStatus.NORMAL)
                .targetCountries(List.of(NoticeCountry.SINGAPORE))
                .readUsers(List.of(3L))
                .build();

        Notice notice = noticeEntity.toDomain();

        assertThat(notice.seq()).isEqualTo(noticeEntity.getSeq());
        assertThat(notice.userSeq()).isEqualTo(noticeEntity.getUserSeq());
        assertThat(notice.title()).isEqualTo(noticeEntity.getTitle());
        assertThat(notice.content()).isEqualTo(noticeEntity.getContent());
        assertThat(notice.type()).isEqualTo(noticeEntity.getType());
        assertThat(notice.status()).isEqualTo(noticeEntity.getStatus());
        assertThat(notice.targetCountries()).isEqualTo(noticeEntity.getTargetCountries());
        assertThat(notice.readUsers()).isEqualTo(noticeEntity.getReadUsers());
    }

    @Test
    @DisplayName("NoticeEntity null 값 처리 테스트")
    void from_handlesNullValues() {
        Notice notice = Notice.builder()
                .seq(null)
                .userSeq(null)
                .title(null)
                .content(null)
                .type(null)
                .status(null)
                .targetCountries(null)
                .readUsers(null)
                .build();

        NoticeEntity noticeEntity = NoticeEntity.from(notice);

        assertThat(noticeEntity.getSeq()).isNull();
        assertThat(noticeEntity.getUserSeq()).isNull();
        assertThat(noticeEntity.getTitle()).isNull();
        assertThat(noticeEntity.getContent()).isNull();
        assertThat(noticeEntity.getType()).isNull();
        assertThat(noticeEntity.getStatus()).isNull();
        assertThat(noticeEntity.getTargetCountries()).isNull();
        assertThat(noticeEntity.getReadUsers()).isNull();
    }

    @Test
    @DisplayName("NoticeEntity toDomain null 값 처리 테스트")
    void toDomain_handlesNullValues() {
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .seq(null)
                .userSeq(null)
                .title(null)
                .content(null)
                .type(null)
                .status(null)
                .targetCountries(null)
                .readUsers(null)
                .build();

        Notice notice = noticeEntity.toDomain();

        assertThat(notice.seq()).isNull();
        assertThat(notice.userSeq()).isNull();
        assertThat(notice.title()).isNull();
        assertThat(notice.content()).isNull();
        assertThat(notice.type()).isNull();
        assertThat(notice.status()).isNull();
        assertThat(notice.targetCountries()).isNull();
        assertThat(notice.readUsers()).isNull();
    }

    @Test
    @DisplayName("NoticeEntity Setter 테스트")
    void setter_test() {
        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title("Title")
                .content("Content")
                .type(NoticeType.NOTICE)
                .status(NoticeStatus.NORMAL)
                .build();

        assertThat(noticeEntity.getTitle()).isEqualTo("Title");
        assertThat(noticeEntity.getContent()).isEqualTo("Content");
        assertThat(noticeEntity.getType()).isEqualTo(NoticeType.NOTICE);
        assertThat(noticeEntity.getStatus()).isEqualTo(NoticeStatus.NORMAL);
    }
}