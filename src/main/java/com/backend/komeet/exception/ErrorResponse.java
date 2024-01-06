package com.backend.komeet.exception;

import lombok.Builder;
import lombok.Getter;

/**
 * 에러 응답
 */
@Builder
@Getter
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
}
