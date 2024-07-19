package com.backend.komeet.user.application;

import com.backend.komeet.base.application.ImageService;
import com.backend.komeet.base.application.RedisService;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.infrastructure.util.CountryUtil;
import com.backend.komeet.infrastructure.util.UUIDUtil;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.enums.UserStatus;
import com.backend.komeet.user.model.dtos.TokenIssuanceDto;
import com.backend.komeet.user.model.dtos.UserSignInDto;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserInfoUpdateRequest;
import com.backend.komeet.user.presentation.request.UserPasswordChangeRequest;
import com.backend.komeet.user.presentation.request.UserPasswordResetRequest;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.backend.komeet.infrastructure.exception.ErrorCode.*;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;

/**
 * 사용자 정보 관련 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;
    private final ImageService imageService;
    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    /**
     * 사용자 정보 수정
     */
    @Transactional
    public void updateInformation(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateRequest userInfoUpdateRequest
    ) {
        User user = getUser(userSeq, "");
        setUserCountryIfItsChanged(country, userInfoUpdateRequest, user);
        setUserNickNameIfItsChanged(userInfoUpdateRequest.getNickName(), user);
        setUserInterestCountryIfItsChanged(userInfoUpdateRequest.getInterestCountry(), user);
        setUserStatusIfItsChanged(userInfoUpdateRequest.getStatus(), user);
        setUserImageProfileIfItsChanged(userInfoUpdateRequest.getProfileImage(), user);
    }

    /**
     * 사용자 비밀번호 초기화
     */
    @Transactional
    public String resetPassword(
            UserPasswordResetRequest userPasswordResetRequest
    ) {
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
     */
    @Transactional
    public void changePassword(
            Long userSeq,
            UserPasswordChangeRequest userPasswordChangeRequest
    ) {
        User user = getUser(userSeq, "");
        String existingPassword = userPasswordChangeRequest.getExistingPassword();
        String newPassword = userPasswordChangeRequest.getNewPassword();
        String currentPassword = user.getPassword();
        if (!passwordEncoder.matches(existingPassword, currentPassword)) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    /**
     * 사용자 닉네임 중복 확인
     */
    @Transactional(readOnly = true)
    public Boolean checkNickname(String nickname) {
        return !userRepository.findByNickName(nickname).isPresent();
    }

    /**
     * 사용자 정보 조회
     */
    @Transactional(readOnly = true)
    public UserSignInDto getUser(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country
    ) {

        User user = getUser(userSeq, "");
        boolean isLocationMatch = false;
        Pair<String, String> countryPair = CountryUtil.fetchLocation(country);
        if (areLocationsSame(user, countryPair)) {
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
     */
    @Transactional
    public Pair<String, String> refreshToken(String token) {
        String userEmail = redisService.getValueByKey(TOKEN_PREFIX + token);
        if (userEmail == null) {
            throw new CustomException(USER_INFO_NOT_FOUND);
        }
        String accessToken = jwtProvider.issueAccessToken(
                TokenIssuanceDto.from(getUser(null, userEmail))
        );
        String refreshToken = jwtProvider.issueRefreshToken();
        redisService.deleteValueByKey(TOKEN_PREFIX + token);
        redisService.saveKeyAndValue(TOKEN_PREFIX + refreshToken, userEmail, REFRESH_TOKEN_EXPIRE_TIME);
        return Pair.of(accessToken, refreshToken);
    }

    /**
     * 사용자 차단
     */
    @Transactional
    public void blockOrUnblockUser(
            Long userSeq,
            Long adminSeq,
            UserStatus status
    ) {
        if (!getUser(adminSeq, "").getUserRole().equals(ROLE_ADMIN)) {
            throw new CustomException(NOT_AN_ADMIN_USER);
        }
        getUser(userSeq, "").setUserStatus(status);
    }

    /**
     * 사용자 정보 조회
     */
    private User getUser(
            Long userSeq,
            String userEmail
    ) {
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

    /**
     * 사용자 나라 변경
     */
    private static void setUserCountryIfItsChanged(
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateRequest userInfoUpdateRequest,
            User user
    ) {
        try {
            if (userInfoUpdateRequest.getCountry() != null && country.get() != null) {
                Pair<String, String> countryPair = CountryUtil.fetchLocation(country);
                user.setRegion(countryPair.getSecond());
                user.setCountry(userInfoUpdateRequest.getCountry());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 사용자 닉네임 변경
     */
    private static void setUserNickNameIfItsChanged(
            String userNickName,
            User user
    ) {
        if (userNickName != null) {
            user.setNickName(userNickName);
        }
    }

    /**
     * 사용자 관심 나라 변경
     */
    private static void setUserInterestCountryIfItsChanged(
            Countries interestCountry,
            User user
    ) {
        if (interestCountry != null) {
            user.setInterestCountry(interestCountry);
        }
    }

    /**
     * 사용자 상태 변경
     */
    private static void setUserStatusIfItsChanged(
            UserStatus status,
            User user
    ) {
        if (status != null) {
            user.setUserStatus(status);
        }
    }

    /**
     * 사용자 프로필 이미지 변경
     */
    private void setUserImageProfileIfItsChanged(
            String profileUrl,
            User user
    ) {
        if (profileUrl != null) {
            imageService.deleteFile(user.getImageUrl());
            user.setImageUrl(profileUrl.isEmpty() ? null : profileUrl);
        }
    }


    /**
     * 위치 정보 일치여부 체크
     */
    private static boolean areLocationsSame(
            User user,
            Pair<String, String> countryPair
    ) {
        return user.getCountry().getCountryName().equals(countryPair.getFirst()) &&
                user.getRegion().equals(countryPair.getSecond());
    }
}
