package com.backend.immilog.user.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.global.exception.ErrorCode.EXISTING_USER;
import static com.backend.immilog.global.exception.ErrorCode.USER_NOT_FOUND;
import static com.backend.immilog.user.enums.UserStatus.ACTIVE;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Pair<Long, String> signUp(
            UserSignUpRequest userSignUpRequest
    ) {
        validateUserNotExists(userSignUpRequest.getEmail());
        String password = passwordEncoder.encode(userSignUpRequest.getPassword());
        User user = userRepository.save(User.of(userSignUpRequest, password));
        return Pair.of(user.getSeq(), user.getNickName());
    }

    @Transactional(readOnly = true)
    public Boolean checkNickname(
            String nickname
    ) {
        return userRepository.findByNickName(nickname).isEmpty();
    }

    @Transactional
    public Pair<String, Boolean> verifyEmail(
            Long userSeq
    ) {
        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        String resultString = "이메일 인증이 완료되었습니다.";
        UserStatus currentUserStatus = user.getUserStatus();
        return getVerificationResult(currentUserStatus, user, resultString);
    }

    private void validateUserNotExists(
            String email
    ) {
        userRepository
                .findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });
    }

    private Pair<String, Boolean> getVerificationResult(
            UserStatus userStatus,
            User user,
            String resultString
    ) {
        boolean isLoginAvailable = true;
        switch (userStatus) {
            case PENDING -> user.setUserStatus(ACTIVE);
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

