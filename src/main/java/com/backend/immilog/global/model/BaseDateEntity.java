package com.backend.immilog.global.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseDateEntity {
    @Setter
    @CreatedDate
    private LocalDateTime createdAt;
    @Setter
    @LastModifiedDate
    private LocalDateTime updatedAt;
}