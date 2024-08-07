package com.backend.komeet.user.application;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.user.enums.UserStatus;
import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserSignUpRequest;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.EXISTING_USER;
import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;
import static com.backend.komeet.user.enums.UserStatus.ACTIVE;

/**
 * 사용자 회원가입 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 회원가입
     */
    @Transactional
    public Pair<Long, String> signUp(
            UserSignUpRequest userSignUpRequest
    ) {
        validateUserNotExists(userSignUpRequest.getEmail());
        String encodedPassword =
                passwordEncoder.encode(userSignUpRequest.getPassword());
        User user = userRepository.save(
                User.from(userSignUpRequest, encodedPassword)
        );
        return Pair.of(user.getSeq(), user.getNickName());
    }

    /**
     * 사용자가 존재하는지 검증
     */
    private void validateUserNotExists(
            String email
    ) {
        userRepository
                .findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });
    }

    /**
     * 이메일로 사용자 정보 가져오기
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(
            String email
    ) {
        return UserDto.from(userRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND)));
    }

    /**
     * 사용자 이메일 인증
     */
    @Transactional
    public Pair<String, Boolean> verifyEmail(
            Long userSeq
    ) {
        User user = getUser(userSeq);
        String resultString = "이메일 인증이 완료되었습니다.";
        UserStatus currentUserStatus = user.getUserStatus();
        return getVerificationResult(currentUserStatus, user, resultString);

    }

    /**
     * 사용자 상태에 따른 인증 결과 가져오기
     */
    private Pair<String, Boolean> getVerificationResult(
            UserStatus currentUserStatus,
            User user,
            String resultString
    ) {
        boolean isLoginAvailable = true;
        switch (currentUserStatus) {
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


    /**
     * 사용자 정보 가져오기
     */
    private User getUser(
            Long userSeq
    ) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
