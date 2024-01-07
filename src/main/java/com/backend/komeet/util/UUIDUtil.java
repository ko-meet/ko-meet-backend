package com.backend.komeet.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UUIDUtil {
    /**
     * UUID 생성
     *
     * @return UUID
     */
    public String generateUUID(Integer length) {
        return java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, length);
    }
}
