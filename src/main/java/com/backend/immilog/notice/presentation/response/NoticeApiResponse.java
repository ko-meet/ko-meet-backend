package com.backend.immilog.notice.presentation.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record NoticeApiResponse(
        Integer status,
        String message,
        Object data,
        List<Object> list
) {
    public static NoticeApiResponse of(Object data) {
        return NoticeApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(null)
                .data(data)
                .list(null)
                .build();
    }
}