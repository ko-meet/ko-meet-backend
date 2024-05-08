package com.backend.komeet.service.user;

import com.backend.komeet.component.RedisDistributedLock;
import com.backend.komeet.domain.Report;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.UserReportRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.ReportRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

import static com.backend.komeet.enums.ReportReason.OTHER;
import static com.backend.komeet.exception.ErrorCode.*;

/**
 * 사용자 신고 관련 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserReportService {
    private final UserRepository userRepository;
    private final RedisDistributedLock redisDistributedLock;
    private final ReportRepository reportRepository;
    final String LOCK_KEY = "reportUser : ";
    final int MAX_RETRY = 3;
    final long RETRY_DELAY_MILLIS = 1000; // 1초

    /**
     * 사용자 신고 유효성 검사
     *
     * @param targetUserSeq   신고대상 고유번호
     * @param reporterUserSeq 신고자 고유 번호
     */
    @Transactional(readOnly = true)
    public void reportValidation(Long targetUserSeq, Long reporterUserSeq) {
        if (checkIfItsMyself(targetUserSeq, reporterUserSeq)) {
            throw new CustomException(CANNOT_REPORT_MYSELF);
        }
        if (reportRepository.existsByReportedUserSeqAndReporterUserSeq(targetUserSeq, reporterUserSeq)) {
            throw new CustomException(ALREADY_REPORTED);
        }
    }

    /**
     * 사용자 신고를 처리하는 메서드
     *
     * @param targetUserSeq   신고 대상 사용자 식별자
     * @param reporterUserSeq 신고자 사용자 식별자
     */
    @Async
    @Transactional
    public void reportUser(Long targetUserSeq,
                           Long reporterUserSeq,
                           UserReportRequest reportUserRequest) {
        if (tryAcquireLock(targetUserSeq)) {
            try {
                processReport(targetUserSeq, reporterUserSeq);
            } finally {
                reportRepository.save(
                        Report.from(
                                targetUserSeq,
                                reporterUserSeq,
                                reportUserRequest,
                                reportUserRequest.getReason().equals(OTHER)
                        )
                );

                redisDistributedLock.releaseLock(LOCK_KEY, targetUserSeq.toString());
            }
        } else {
            log.error(
                    "Failed to acquire lock for userSeq: {} after {} attempts",
                    targetUserSeq,
                    MAX_RETRY
            );
        }
    }

    /**
     * 자기 자신을 신고하는지 확인
     *
     * @param targetUserSeq   신고대상 고유번호
     * @param reporterUserSeq 신고자 고유 번호
     * @return boolean
     */
    private static boolean checkIfItsMyself(Long targetUserSeq,
                                            Long reporterUserSeq) {
        return targetUserSeq.equals(reporterUserSeq);
    }

    /**
     * 분산 락을 시도하고 결과를 반환
     *
     * @param targetUserSeq 신고 대상 사용자 식별자
     * @return 락 획득 여부
     */
    private boolean tryAcquireLock(Long targetUserSeq) {
        boolean lockAcquired = false;
        int retryCount = 0;
        while (!lockAcquired && retryCount < MAX_RETRY) {
            lockAcquired = redisDistributedLock.acquireLock(
                    LOCK_KEY, targetUserSeq.toString(), 10
            );
            if (!lockAcquired) {
                try {
                    Thread.sleep(RETRY_DELAY_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                retryCount++;
            }
        }
        return lockAcquired;
    }

    /**
     * 신고 로직을 처리
     *
     * @param targetUserSeq   신고 대상 사용자 식별자
     * @param reporterUserSeq 신고자 사용자 식별자
     */
    private void processReport(Long targetUserSeq, Long reporterUserSeq) {
        User targetUser = getUser(targetUserSeq);

        long reportCount =
                targetUser.getReportedCount() == null ?
                0 : targetUser.getReportedCount();
        targetUser.setReportedCount(reportCount + 1);
        targetUser.setReportedDate(Date.valueOf(LocalDate.now()));
        userRepository.save(targetUser);
        log.info("User {} reported by {}", targetUserSeq, reporterUserSeq);
    }

    /**
     * 사용자 정보를 조회
     *
     * @param targetUserSeq 사용자 식별자
     * @return {@link User}
     */
    private User getUser(Long targetUserSeq) {
        return userRepository
                .findById(targetUserSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
