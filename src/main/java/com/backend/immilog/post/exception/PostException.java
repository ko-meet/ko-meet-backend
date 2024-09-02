package com.backend.immilog.post.exception;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.exception.ErrorCode;

public class PostException extends CustomException {
    public PostException(ErrorCode e) {
        super(e);
    }
}
