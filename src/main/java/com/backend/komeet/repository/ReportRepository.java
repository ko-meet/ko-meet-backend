package com.backend.komeet.repository;

import com.backend.komeet.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 신고 레포지토리
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReportedUserSeqAndReporterUserSeq(Long targetUserSeq,
                                                      Long reporterUserSeq);
}
