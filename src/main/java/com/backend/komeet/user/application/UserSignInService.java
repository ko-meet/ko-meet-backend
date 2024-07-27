package com.backend.komeet.user.application;

import com.backend.komeet.base.application.RedisService;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.infrastructure.util.CountryUtil;
import com.backend.komeet.user.enums.UserStatus;
import com.backend.komeet.user.model.dtos.TokenIssuanceDto;
import com.backend.komeet.user.model.dtos.UserSignInDto;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserSignInRequest;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.infrastructure.exception.ErrorCode.*;

/**
 * 사용자 로그인 서비스
 */
@RequiredArgsConstructor
@Service
public class UserSignInService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    /**
     * 사용자 로그인
     */
    public UserSignInDto signIn(
            UserSignInRequest userSignInRequest,
            CompletableFuture<Pair<String, String>> country
    ) {
        User user = getUser(userSignInRequest);
        validateIfPasswordsMatches(userSignInRequest, user);
        validateIfUserStateIsActive(user);
        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDto.from(user));
        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        Pair<String, String> countryPair = CountryUtil.fetchLocation(country);

        return UserSignInDto.from(
                user,
                accessToken,
                refreshToken,
                isLocationMatch(user, countryPair)
        );
    }

    /**
     * 사용자 위치 정보 일치여부 체크
     */
    private static boolean isLocationMatch(
            User user,
            Pair<String, String> countryPair
    ) {
        return user.getCountry().getCountryName().equals(countryPair.getFirst()) &&
                user.getRegion().equals(countryPair.getSecond());
    }

    /**
     * 사용자 상태가 활성화 상태인지 체크
     */
    private static void validateIfUserStateIsActive(
            User user
    ) {
        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new CustomException(USER_STATUS_NOT_ACTIVE);
        }
    }

    /**
     * 사용자 비밀번호 일치여부 체크
     */
    private void validateIfPasswordsMatches(
            UserSignInRequest userSignInRequest,
            User user
    ) {
        if (!passwordEncoder.matches(userSignInRequest.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 사용자 정보 가져오기
     */
    private User getUser(
            UserSignInRequest userSignInRequest
    ) {
        return userRepository
                .findByEmail(userSignInRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

}
