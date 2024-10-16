package com.backend.immilog.global.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseDateEntity 클래스 테스트")
class BaseDateEntityTest {
    private final BaseDateEntity baseDateEntity = new BaseDateEntity() {
    };

    @Test
    @DisplayName("생성일자가 설정되는지 테스트")
    void createdAtIsSet() {
        LocalDateTime now = LocalDateTime.now();
        baseDateEntity.setCreatedAt(now);
        assertThat(baseDateEntity.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("수정일자가 설정되는지 테스트")
    void updatedAtIsSet() {
        LocalDateTime now = LocalDateTime.now();
        baseDateEntity.setUpdatedAt(now);

        assertThat(baseDateEntity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("생성일자와 수정일자가 null인 경우")
    void createdAtAndUpdatedAtAreNullInitially() {
        assertThat(baseDateEntity.getCreatedAt()).isNull();
        assertThat(baseDateEntity.getUpdatedAt()).isNull();
    }
}