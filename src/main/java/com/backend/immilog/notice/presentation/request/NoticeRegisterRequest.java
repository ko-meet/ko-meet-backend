package com.backend.immilog.notice.presentation.request;

import com.backend.immilog.notice.application.command.NoticeUploadCommand;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "공지사항 생성 요청 서비스 DTO")
public record NoticeRegisterRequest(
        String title,
        String content,
        NoticeType type,
        List<NoticeCountry> targetCountries
) {
    public NoticeUploadCommand toCommand() {
        return NoticeUploadCommand.builder()
                .title(title)
                .content(content)
                .type(type)
                .targetCountries(targetCountries)
                .build();
    }
}