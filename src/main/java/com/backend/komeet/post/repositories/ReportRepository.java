package com.backend.komeet.post.repositories;

import com.backend.komeet.user.model.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 신고 레포지토리
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReportedUserSeqAndReporterUserSeq(
            Long targetUserSeq,
            Long reporterUserSeq
    );
}
