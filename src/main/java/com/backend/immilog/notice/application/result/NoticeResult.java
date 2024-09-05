package com.backend.immilog.notice.application.result;

import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeResult(
        Long seq,
        Long authorUserSeq,
        String title,
        String content,
        NoticeType type,
        NoticeStatus status,
        List<NoticeCountry> targetCountries,
        List<Long> readUsers,
        LocalDateTime createdAt
) {
    public static NoticeResult from(
            Notice notice
    ) {
        return NoticeResult.builder()
                .seq(notice.seq())
                .authorUserSeq(notice.userSeq())
                .title(notice.title())
                .content(notice.content())
                .type(notice.type())
                .status(notice.status())
                .targetCountries(notice.targetCountries())
                .readUsers(notice.readUsers())
                .createdAt(notice.createdAt())
                .build();
    }
}

