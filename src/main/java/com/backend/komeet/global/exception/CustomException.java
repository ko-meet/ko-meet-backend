package com.backend.komeet.global.exception;

import lombok.Getter;

/**
 * 커스텀 예외
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 받아서 예외를 생성한다.
     */
    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.errorCode = e;
    }
}
