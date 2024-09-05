package com.backend.immilog.notice.infrastructure.jpa.repositories;

import com.backend.immilog.notice.infrastructure.jpa.entities.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {
}
