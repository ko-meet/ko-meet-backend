package com.backend.immilog.global.presentation.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record ApiResponse(
        Integer status,
        String message,
        Object data,
        List<Object> list
) {
    public static ApiResponse of(
            Object data
    ) {
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(null)
                .data(data)
                .list(null)
                .build();
    }
}
