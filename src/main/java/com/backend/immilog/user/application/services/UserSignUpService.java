package com.backend.immilog.user.application.services;

import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.user.domain.model.enums.UserStatus.ACTIVE;
import static com.backend.immilog.user.exception.UserErrorCode.EXISTING_USER;
import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Pair<Long, String> signUp(
            UserSignUpCommand userSignUpCommand
    ) {
        validateUserNotExists(userSignUpCommand.email());
        String password = passwordEncoder.encode(userSignUpCommand.password());
        User user = userRepository.saveEntity(User.of(userSignUpCommand, password));
        return Pair.of(user.seq(), user.nickName());
    }

    @Transactional(readOnly = true)
    public Boolean checkNickname(
            String nickname
    ) {
        return userRepository.getByUserNickname(nickname).isEmpty();
    }

    @Transactional
    public Pair<String, Boolean> verifyEmail(
            Long userSeq
    ) {
        User user = userRepository.getById(userSeq).orElseThrow(() -> new UserException(USER_NOT_FOUND));
        String resultString = "이메일 인증이 완료되었습니다.";
        UserStatus currentUserStatus = user.userStatus();
        return getVerificationResult(currentUserStatus, user, resultString);
    }

    private void validateUserNotExists(
            String email
    ) {
        userRepository
                .getByEmail(email)
                .ifPresent(user -> {
                    throw new UserException(EXISTING_USER);
                });
    }

    private Pair<String, Boolean> getVerificationResult(
            UserStatus userStatus,
            User user,
            String resultString
    ) {
        boolean isLoginAvailable = true;
        switch (userStatus) {
            case PENDING -> userRepository.saveEntity(user.copyWithNewUserStatus(ACTIVE));
            case ACTIVE -> resultString = "이미 인증된 사용자입니다.";
            case BLOCKED -> {
                resultString = "차단된 사용자입니다.";
                isLoginAvailable = false;
            }
            default -> resultString = "이메일 인증이 필요한 사용자가 아닙니다.";
        }
        return Pair.of(resultString, isLoginAvailable);
    }
}

