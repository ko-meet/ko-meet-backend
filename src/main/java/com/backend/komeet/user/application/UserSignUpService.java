package com.backend.komeet.user.application;

import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.user.presentation.request.UserSignUpRequest;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.user.enums.UserStatus.ACTIVE;
import static com.backend.komeet.infrastructure.exception.ErrorCode.EXISTING_USER;
import static com.backend.komeet.infrastructure.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
     *
     * @param userSignUpRequest 사용자 회원가입 요청
     */
    @Transactional
    public Pair<Long, String> signUp(UserSignUpRequest userSignUpRequest) {
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
     *
     * @param email 사용자 이메일
     */
    private void validateUserNotExists(String email) {
        userRepository
                .findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });
    }

    /**
     * 이메일로 사용자 정보 가져오기
     *
     * @param email 사용자 이메일
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return UserDto.from(userRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND)));
    }

    /**
     * 사용자 이메일 인증
     *
     * @param userSeq 사용자 시퀀스
     */
    @Transactional
    public void verifyEmail(Long userSeq) {
        User user = getUser(userSeq);
        user.setUserStatus(ACTIVE);
    }

    /**
     * 사용자 정보 가져오기
     *
     * @param userSeq 사용자 시퀀스
     * @return {@link User}
     */
    private User getUser(Long userSeq) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
