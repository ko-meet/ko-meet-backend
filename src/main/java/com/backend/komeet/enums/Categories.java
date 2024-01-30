package com.backend.komeet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
