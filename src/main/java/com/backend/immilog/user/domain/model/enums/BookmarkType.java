package com.backend.immilog.user.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkType {
    POST("post"),
    JOB_BOARD("job_board");

    private final String value;

    public static BookmarkType convertToEnum(
            String bookMarkType
    ) {
        return switch (bookMarkType) {
            case "post" -> POST;
            case "job_board" -> JOB_BOARD;
            default -> throw new IllegalStateException("Unexpected value: " + bookMarkType);
        };
    }
}
