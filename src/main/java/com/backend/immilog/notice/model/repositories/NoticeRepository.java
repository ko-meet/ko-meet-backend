package com.backend.immilog.notice.model.repositories;

import com.backend.immilog.notice.model.entities.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeRepositoryCustom {
}
