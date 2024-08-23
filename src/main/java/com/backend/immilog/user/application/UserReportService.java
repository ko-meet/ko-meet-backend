package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisDistributedLock;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.user.model.interfaces.repositories.ReportRepository;
import com.backend.immilog.user.model.interfaces.repositories.UserRepository;
import com.backend.immilog.user.model.entities.Report;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserReportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.backend.immilog.global.exception.ErrorCode.*;
import static com.backend.immilog.user.enums.ReportReason.OTHER;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserReportService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final RedisDistributedLock redisDistributedLock;
    final String LOCK_KEY = "reportUser : ";

    @Async
    @Transactional
    public void reportUser(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportRequest reportUserRequest
    ) {
        reportValidation(targetUserSeq, reporterUserSeq);
        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.tryAcquireLock(LOCK_KEY, targetUserSeq.toString());
            if (lockAcquired) {
                processReport(targetUserSeq, reporterUserSeq, reportUserRequest);
            } else {
                log.error("사용자 신고 실패 - 원인: 락 획득 실패  targetUserSeq: {}, time: {}",
                        targetUserSeq, LocalDateTime.now());
            }
        } finally {
            if (lockAcquired) {
                redisDistributedLock.releaseLock(LOCK_KEY, targetUserSeq.toString());
            }
        }
    }

    private void processReport(
            Long targetUserSeq,
            Long reporterUserSeq,
            UserReportRequest userReportRequest
    ) {
        User targetUser = getUser(targetUserSeq);
        long reportCount = getReportCount(targetUser);
        targetUser.getReportInfo().setReportedCount(reportCount + 1);
        targetUser.getReportInfo().setReportedDate(Date.valueOf(LocalDate.now()));
        reportRepository.save(
                Report.of(
                        targetUserSeq,
                        reporterUserSeq,
                        userReportRequest,
                        userReportRequest.reason().equals(OTHER)
                )
        );
        log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
    }

    private static long getReportCount(
            User targetUser
    ) {
        return targetUser.getReportInfo().getReportedCount() == null ?
                0 : targetUser.getReportInfo().getReportedCount();
    }

    private void reportValidation(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        validateDifferentUsers(targetUserSeq, reporterUserSeq);
        validateItsNotDuplicatedReport(targetUserSeq, reporterUserSeq);
    }

    private void validateItsNotDuplicatedReport(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        if (targetUserSeq.equals(reporterUserSeq)) {
            throw new CustomException(CANNOT_REPORT_MYSELF);
        }
    }

    private void validateDifferentUsers(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        boolean isExist =
                reportRepository.existsByReportedUserSeqAndReporterUserSeq(
                        targetUserSeq, reporterUserSeq
                );
        if (isExist) {
            throw new CustomException(ALREADY_REPORTED);
        }
    }

    private User getUser(
            Long targetUserSeq
    ) {
        return userRepository
                .findById(targetUserSeq)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
