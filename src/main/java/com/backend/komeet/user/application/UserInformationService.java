package com.backend.komeet.user.application;

import com.backend.komeet.base.application.RedisService;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.infrastructure.util.CountryUtil;
import com.backend.komeet.infrastructure.util.UUIDUtil;
import com.backend.komeet.user.enums.UserStatus;
import com.backend.komeet.user.model.dtos.TokenIssuanceDto;
import com.backend.komeet.user.model.dtos.UserSignInDto;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserInfoUpdateRequest;
import com.backend.komeet.user.presentation.request.UserPasswordChangeRequest;
import com.backend.komeet.user.presentation.request.UserPasswordResetRequest;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.infrastructure.exception.ErrorCode.*;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;
import static com.backend.komeet.user.enums.UserStatus.BLOCKED;

/**
 * 사용자 정보 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    /**
     * 사용자 정보 수정
     *
     * @param userSeq               사용자 번호
     * @param country               나라 정보
     * @param userInfoUpdateRequest 사용자 정보 수정 요청
     */
    @Transactional
    public void updateInformation(Long userSeq,
                                  CompletableFuture<Pair<String, String>> country,
                                  UserInfoUpdateRequest userInfoUpdateRequest) {

        User user = getUser(userSeq, "");

        if (userInfoUpdateRequest.getNickName() != null) {
            user.setNickName(userInfoUpdateRequest.getNickName());
        }
        if (userInfoUpdateRequest.getCountry() != null) {
            Pair<String, String> countryPair =
                    CountryUtil.fetchLocation(country);
            user.setRegion(countryPair.getSecond());
            user.setCountry(userInfoUpdateRequest.getCountry());
        }
        if (userInfoUpdateRequest.getProfileImage() != null) {
            user.setImageUrl(userInfoUpdateRequest.getProfileImage());
        }
        if (userInfoUpdateRequest.getStatus() != null) {
            user.setUserStatus(userInfoUpdateRequest.getStatus());
        }

    }

    /**
     * 사용자 비밀번호 초기화
     *
     * @param userPasswordResetRequest 사용자 번호
     * @return 사용자 번호
     */
    @Transactional
    public String resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        User user = getUser(null, userPasswordResetRequest.getEmail());

        if (!user.getCountry().equals(userPasswordResetRequest.getCountry())) {
            throw new CustomException(USER_INFO_NOT_FOUND);
        }

        String temporaryPassword = UUIDUtil.generateUUID(10);
        user.setPassword(passwordEncoder.encode(temporaryPassword));

        return temporaryPassword;
    }

    /**
     * 사용자 비밀번호 변경
     *
     * @param userSeq                   사용자 번호
     * @param userPasswordChangeRequest {@link UserPasswordChangeRequest}
     */
    @Transactional
    public void changePassword(Long userSeq,
                               UserPasswordChangeRequest userPasswordChangeRequest) {
        User user = getUser(userSeq, "");

        if (!passwordEncoder.matches(
                userPasswordChangeRequest.getExistingPassword(), user.getPassword())
        ) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        user.setPassword(
                passwordEncoder.encode(userPasswordChangeRequest.getNewPassword())
        );

    }

    /**
     * 사용자 닉네임 중복 확인
     *
     * @param nickname 닉네임
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        return !userRepository.findByNickName(nickname).isPresent();
    }

    /**
     * 사용자 정보 조회
     *
     * @param userSeq 사용자 번호
     * @param country CompletableFuture 객체
     * @return 사용자 정보
     */
    @Transactional(readOnly = true)
    public UserSignInDto getUser(Long userSeq, CompletableFuture<Pair<String, String>> country) {

        User user = getUser(userSeq, "");

        boolean isLocationMatch = false;

        Pair<String, String> countryPair = CountryUtil.fetchLocation(country);

        if (user.getCountry().getCountryName().equals(countryPair.getFirst()) &&
                user.getRegion().equals(countryPair.getSecond())) {
            isLocationMatch = true;
        }

        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDto.from(user));
        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        return UserSignInDto.from(
                user, accessToken, refreshToken, isLocationMatch
        );
    }

    /**
     * 토큰 재발급
     *
     * @param token 토큰
     * @return 토큰
     */
    @Transactional
    public Pair<String, String> refreshToken(String token) {
        String userEmail = redisService.getValueByKey(TOKEN_PREFIX + token);
        if (userEmail == null) {
            throw new CustomException(USER_INFO_NOT_FOUND);
        }
        String accessToken = jwtProvider.issueAccessToken(
                TokenIssuanceDto.from(
                        userRepository
                                .findByEmail(userEmail)
                                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND))
                )
        );

        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.deleteValueByKey(TOKEN_PREFIX + token);
        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);

        return Pair.of(accessToken, refreshToken);
    }

    /**
     * 사용자 차단
     *
     * @param userSeq  차단대상 사용자 고유번호
     * @param adminSeq 관리자 고유번호
     */
    @Transactional
    public void blockOrUnblockUser(Long userSeq,
                                   Long adminSeq,
                                   UserStatus status) {
        if (!getUser(adminSeq, "").getUserRole().equals(ROLE_ADMIN)) {
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
        getUser(userSeq, "").setUserStatus(status);
    }

    /**
     * 사용자 정보 조회
     *
     * @param userSeq   사용자 고유번호
     * @param userEmail 사용자 이메일
     * @return {@link User}
     */
    private User getUser(Long userSeq, String userEmail) {
        if (userSeq != null) {
            return userRepository
                    .findById(userSeq)
                    .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        } else if (userEmail != null) {
            return userRepository
                    .findByEmail(userEmail)
                    .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
        }
        return new User();
    }
}
