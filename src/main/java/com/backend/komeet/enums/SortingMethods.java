package com.backend.komeet.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortingMethods {
    CREATED_DATE("createdDate"),
    VIEW_COUNT("viewCount"),
    LIKE_COUNT("likeCount"),
    COMMENT_COUNT("commentCount");

    private final String sortingMethod;

    public static SortingMethods getSortingMethod(String sortingMethod) {
        for (SortingMethods method : SortingMethods.values()) {
            if (method.getSortingMethod().equals(sortingMethod)) {
                return method;
            }
        }
        return null;
    }

    private Object getSortingMethod() {
        return null;
    }
}
