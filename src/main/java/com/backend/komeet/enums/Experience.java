package com.backend.komeet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Experience {
    JUNIOR("신입"),
    JUNIOR2("신입/경력"),
    MIDDLE("경력"),
    SENIOR("고급"),
    DIRECTOR("이사"),
    EXECUTIVE("임원");
    private final String experience;
}
