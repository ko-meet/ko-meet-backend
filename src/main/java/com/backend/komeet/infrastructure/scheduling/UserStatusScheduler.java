package com.backend.komeet.infrastructure.scheduling;

import com.backend.komeet.user.application.UserInformationService;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Component
public class UserStatusScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateBlockedUsersStatus() {
        log.info(
                "Blocked users: {}",
                userRepository.updateStatusToDeletedForReportedUsers()
        );
    }
}