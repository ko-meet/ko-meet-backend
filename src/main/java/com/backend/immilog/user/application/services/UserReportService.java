package com.backend.immilog.user.application.services;

import com.backend.immilog.global.infrastructure.lock.RedisDistributedLock;
import com.backend.immilog.user.application.command.UserReportCommand;
import com.backend.immilog.user.domain.model.Report;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.repositories.ReportRepository;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.backend.immilog.user.domain.model.enums.ReportReason.OTHER;
import static com.backend.immilog.user.exception.UserErrorCode.*;

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
            UserReportCommand userReportCommand
    ) {
        reportValidation(targetUserSeq, reporterUserSeq);
        boolean lockAcquired = false;
        try {
            lockAcquired = redisDistributedLock.tryAcquireLock(LOCK_KEY, targetUserSeq.toString());
            if (lockAcquired) {
                processReport(targetUserSeq, reporterUserSeq, userReportCommand);
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
            UserReportCommand userReportCommand
    ) {
        User targetUser = getUser(targetUserSeq);
        long reportCount = getReportCount(targetUser);
        targetUser.reportInfo().setReportedCount(reportCount + 1);
        targetUser.reportInfo().setReportedDate(Date.valueOf(LocalDate.now()));
        reportRepository.saveEntity(
                Report.of(
                        targetUserSeq,
                        reporterUserSeq,
                        userReportCommand,
                        userReportCommand.reason().equals(OTHER)
                )
        );
        log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
    }

    private static long getReportCount(
            User targetUser
    ) {
        return targetUser.reportInfo().getReportedCount() == null ?
                0 : targetUser.reportInfo().getReportedCount();
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
            throw new UserException(CANNOT_REPORT_MYSELF);
        }
    }

    private void validateDifferentUsers(
            Long targetUserSeq,
            Long reporterUserSeq
    ) {
        boolean isExist =
                reportRepository.existsByUserSeqNumbers(
                        targetUserSeq, reporterUserSeq
                );
        if (isExist) {
            throw new UserException(ALREADY_REPORTED);
        }
    }

    private User getUser(
            Long targetUserSeq
    ) {
        return userRepository
                .getById(targetUserSeq)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
