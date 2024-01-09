package com.backend.komeet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageDirectory {
    PROFILE("profile"),
    POSTING_IMAGE("posting-image");
    private final String string;
}