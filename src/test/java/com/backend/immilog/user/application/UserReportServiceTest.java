package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisDistributedLock;
import com.backend.immilog.user.application.services.UserReportService;
import com.backend.immilog.user.enums.ReportReason;
import com.backend.immilog.user.exception.UserException;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.embeddables.ReportInfo;
import com.backend.immilog.user.model.entities.Report;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.model.repositories.ReportRepository;
import com.backend.immilog.user.model.repositories.UserRepository;
import com.backend.immilog.user.presentation.request.UserReportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.backend.immilog.global.enums.UserRole.ROLE_USER;
import static com.backend.immilog.user.exception.UserErrorCode.ALREADY_REPORTED;
import static com.backend.immilog.user.exception.UserErrorCode.CANNOT_REPORT_MYSELF;
import static com.backend.immilog.user.model.enums.UserCountry.MALAYSIA;
import static com.backend.immilog.user.model.enums.UserCountry.SOUTH_KOREA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("사용자 신고 서비스 테스트")
class UserReportServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private RedisDistributedLock redisDistributedLock;
    private com.backend.immilog.user.application.services.UserReportService userReportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userReportService = new UserReportService(
                userRepository,
                reportRepository,
                redisDistributedLock
        );
    }

    @Test
    @DisplayName("사용자 신고 성공")
    void reportUser() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 2L;
        User user = User.builder()
                .seq(targetUserSeq)
                .email("test@email.com")
                .nickName("test")
                .imageUrl("image")
                .userStatus(UserStatus.PENDING)
                .userRole(ROLE_USER)
                .interestCountry(SOUTH_KOREA)
                .location(Location.of(MALAYSIA, "KL"))
                .reportInfo(
                        ReportInfo.of(
                                1L,
                                Date.valueOf(LocalDateTime.now().toLocalDate()))
                )
                .build();
        UserReportRequest reportUserRequest = UserReportRequest.builder()
                .description("test")
                .reason(ReportReason.FRAUD)
                .build();
        when(reportRepository.existsByReportedUserSeqAndReporterUserSeq(
                targetUserSeq, reporterUserSeq
        )).thenReturn(false);
        when(redisDistributedLock.tryAcquireLock("reportUser : ", targetUserSeq.toString()))
                .thenReturn(true);
        when(userRepository.findById(targetUserSeq))
                .thenReturn(Optional.of(user));
        // when
        userReportService.reportUser(
                targetUserSeq,
                reporterUserSeq,
                reportUserRequest.toCommand()
        );

        // then
        verify(redisDistributedLock, times(1))
                .tryAcquireLock("reportUser : ", targetUserSeq.toString());
        verify(redisDistributedLock, times(1))
                .releaseLock("reportUser : ", targetUserSeq.toString());
        verify(reportRepository, times(1))
                .save(any(Report.class));
    }

    @Test
    @DisplayName("사용자 신고 실패: 본인 신고")
    void reportUser_failed_himself() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 1L;
        UserReportRequest reportUserRequest = UserReportRequest.builder().build();
        when(reportRepository.existsByReportedUserSeqAndReporterUserSeq(
                targetUserSeq, reporterUserSeq
        )).thenReturn(false);
        // when & then
        assertThatThrownBy(() -> userReportService.reportUser(
                targetUserSeq,
                reporterUserSeq,
                reportUserRequest.toCommand()
        ))
                .isInstanceOf(UserException.class)
                .hasMessage(CANNOT_REPORT_MYSELF.getMessage());
    }

    @Test
    @DisplayName("사용자 신고 실패: 중복 신고")
    void reportUser_failed_duplicated() {
        // given
        Long targetUserSeq = 1L;
        Long reporterUserSeq = 2L;
        UserReportRequest reportUserRequest = UserReportRequest.builder().build();
        when(reportRepository.existsByReportedUserSeqAndReporterUserSeq(
                targetUserSeq, reporterUserSeq
        )).thenReturn(true);
        // when & then
        assertThatThrownBy(() -> userReportService.reportUser(
                targetUserSeq,
                reporterUserSeq,
                reportUserRequest.toCommand()
        ))
                .isInstanceOf(UserException.class)
                .hasMessage(ALREADY_REPORTED.getMessage());
    }
}