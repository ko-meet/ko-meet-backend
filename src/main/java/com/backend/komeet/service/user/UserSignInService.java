package com.backend.komeet.service.user;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.TokenIssuanceDto;
import com.backend.komeet.dto.UserSignInDto;
import com.backend.komeet.dto.request.UserSignInRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.UserRepository;
import com.backend.komeet.service.external.RedisService;
import com.backend.komeet.util.CountryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.enums.UserStatus.ACTIVE;
import static com.backend.komeet.exception.ErrorCode.*;

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
     *
     * @param userSignInRequest 사용자 로그인 요청
     * @return 사용자 로그인 응답
     */
    public UserSignInDto signIn(UserSignInRequest userSignInRequest,
                                CompletableFuture<Pair<String, String>> country) {

        User user = userRepository.findByEmail(userSignInRequest.getEmail())
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        if (!passwordEncoder.matches(userSignInRequest.getPassword(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }

        if (!user.getUserStatus().equals(ACTIVE)) {
            throw new CustomException(USER_STATUS_NOT_ACTIVE);
        }

        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDto.from(user));
        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(), REFRESH_TOKEN_EXPIRE_TIME
        );

        Pair<String, String> countryPair = CountryUtil.fetchLocation(country);

        return UserSignInDto.from(
                user,
                accessToken,
                refreshToken,
                user.getCountry().getCountryName().equals(countryPair.getFirst()) &&
                        user.getRegion().equals(countryPair.getSecond())
        );
    }

}
