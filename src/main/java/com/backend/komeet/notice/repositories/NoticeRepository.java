package com.backend.komeet.notice.repositories;

import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.user.enums.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 공지사항 레포지토리
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long>, NoticeQRepository {
    Boolean existsByTargetCountriesContainingAndReadUsersNotContaining(
            Countries country,
            Long seq
    );
}
