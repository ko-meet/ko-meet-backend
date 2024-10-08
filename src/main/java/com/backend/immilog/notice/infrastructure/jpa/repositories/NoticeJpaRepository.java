package com.backend.immilog.notice.infrastructure.jpa.repositories;

import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.infrastructure.jpa.entities.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {
    Boolean existsByTargetCountriesContainingAndReadUsersNotContaining(
            NoticeCountry country,
            Long seq
    );
}
