package com.backend.immilog.notice.exception;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.exception.ErrorCode;

public class NoticeException extends CustomException {
    public NoticeException(ErrorCode e) {
        super(e);
    }
}
