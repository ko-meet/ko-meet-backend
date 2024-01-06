package com.backend.komeet.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.UserDto;
import com.backend.komeet.dto.request.UserSignUpRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.komeet.enums.UserStatus.ACTIVE;
import static com.backend.komeet.exception.ErrorCode.*;

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
     * @param country
     */
    @Transactional
    public Long signUp(UserSignUpRequest userSignUpRequest,
                       CompletableFuture<Pair<String, String>> country) {

        validateUserNotExists(userSignUpRequest.getEmail());

        Pair<String, String> response = fetchLocation(country);

        String encodedPassword =
                passwordEncoder.encode(userSignUpRequest.getPassword());
        String region =
                extractCity(response, userSignUpRequest.getCountry().getCountryName());

        User user = userRepository.save(
                User.from(userSignUpRequest, encodedPassword, region)
        );

        return user.getSeq();
    }

    /**
     * 사용자가 존재하는지 검증
     *
     * @param email 사용자 이메일
     */
    private void validateUserNotExists(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EXISTING_USER);
                });
    }

    /**
     * 사용자의 위치 정보를 가져옴
     *
     * @param country CompletableFuture 객체
     * @return 나라와 도시 정보
     */
    private Pair<String, String> fetchLocation(
            CompletableFuture<Pair<String, String>> country) {

        return country
                .orTimeout(5, TimeUnit.SECONDS) // 5초 이내에 완료되지 않으면 타임아웃
                .exceptionally(throwable -> Pair.of("Error", "Timeout"))
                .join();
    }

    /**
     * 도시 정보를 추출
     *
     * @param response 외부 API 응답
     * @return 도시 정보
     */
    private String extractCity(Pair<String, String> response, String country) {
        if (!response.getFirst().equals(country)) {
            throw new CustomException(LOCATION_NOT_MATCH);
        }
        return response.getSecond();
    }

    /**
     * 이메일로 사용자 정보 가져오기
     *
     * @param email 사용자 이메일
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        return UserDto.from(userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND)));
    }

    /**
     * 사용자 이메일 인증
     * @param userSeq 사용자 시퀀스
     */
    @Transactional
    public void verifyEmail(Long userSeq) {
        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        user.setUserStatus(ACTIVE);
    }
}
