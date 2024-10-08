package com.backend.immilog.notice.application.command;

import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.model.enums.NoticeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "공지사항 생성 요청 서비스 DTO")
public record NoticeUploadCommand(
        String title,
        String content,
        NoticeType type,
        List<NoticeCountry> targetCountries
) {
}
