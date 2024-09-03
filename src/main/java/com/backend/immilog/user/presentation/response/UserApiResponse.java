package com.backend.immilog.user.presentation.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
public record UserApiResponse(
        Integer status,
        String message,
        Object data,
        List<Object> list
) {
    public static UserApiResponse of(Object data) {
        return UserApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message(null)
                .data(data)
                .list(null)
                .build();
    }
}
