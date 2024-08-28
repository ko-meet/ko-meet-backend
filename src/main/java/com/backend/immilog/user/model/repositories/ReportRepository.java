package com.backend.immilog.user.model.repositories;

import com.backend.immilog.user.model.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReportedUserSeqAndReporterUserSeq(
            Long targetUserSeq,
            Long reporterUserSeq
    );
}
