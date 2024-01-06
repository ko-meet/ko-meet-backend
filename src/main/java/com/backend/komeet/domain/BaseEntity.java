package com.backend.komeet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 속성을 정의하는 추상 클래스
 */
@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
