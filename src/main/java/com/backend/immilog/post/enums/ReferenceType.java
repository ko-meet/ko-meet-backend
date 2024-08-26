package com.backend.immilog.post.enums;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ReferenceType {
    COMMENT("comments"),
    POST("posts");

    private final String stringValue;

    public static ReferenceType getByString(
            String referenceType
    ) {
        return Arrays.stream(values())
                .filter(type -> type.stringValue.equals(referenceType))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFERENCE_TYPE));
    }
}
