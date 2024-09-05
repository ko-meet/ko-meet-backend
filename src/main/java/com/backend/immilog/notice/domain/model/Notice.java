package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.application.command.NoticeUploadCommand;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeStatus;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record Notice(
        Long seq,
        Long userSeq,
        String title,
        String content,
        NoticeType type,
        NoticeStatus status,
        List<NoticeCountry> targetCountries,
        List<Long> readUsers,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static Notice of(
            Long userSeq,
            NoticeUploadCommand command
    ) {
        return Notice.builder()
                .title(command.title())
                .userSeq(userSeq)
                .content(command.content())
                .type(command.type())
                .targetCountries(command.targetCountries())
                .status(NoticeStatus.NORMAL)
                .build();
    }

}


