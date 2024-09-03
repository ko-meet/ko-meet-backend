package com.backend.immilog.post.presentation.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record PostApiResponse(
        Integer status,
        String message,
        Object data,
        List<Object> list
) {
    public static PostApiResponse of(Object data) {
        return PostApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(null)
                .data(data)
                .list(null)
                .build();
    }
}
