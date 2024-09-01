package com.backend.immilog.notice.model.dtos;

import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.enums.Countries;
import com.backend.immilog.notice.model.enums.NoticeStatus;
import com.backend.immilog.notice.model.enums.NoticeType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record NoticeDTO(
        Long seq,
        Long authorUserSeq,
        String title,
        String content,
        NoticeType type,
        NoticeStatus status,
        List<Countries> targetCountries,
        List<Long> readUsers,
        LocalDateTime createdAt
) {
    public static NoticeDTO from(
            Notice notice
    ) {
        return NoticeDTO.builder()
                .seq(notice.getSeq())
                .authorUserSeq(notice.getUserSeq())
                .title(notice.getTitle())
                .content(notice.getContent())
                .type(notice.getType())
                .status(notice.getStatus())
                .targetCountries(notice.getTargetCountries())
                .readUsers(notice.getReadUsers())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}

