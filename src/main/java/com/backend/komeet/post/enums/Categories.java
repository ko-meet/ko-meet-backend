package com.backend.komeet.post.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 카테고리 관련 상수
 */
@Getter
@RequiredArgsConstructor
public enum Categories {
    ALL("all"),
    WORKING_HOLIDAY("workingHoliday"),
    GREEN_CARD("greenCard"),
    COMMUNICATION("communication"),
    QNA("qna");

    private final String category;
}
