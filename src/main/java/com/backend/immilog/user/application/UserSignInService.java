package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.model.TokenIssuanceDTO;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.interfaces.repositories.UserRepository;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserSignInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.immilog.global.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserSignInService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    @Transactional
    public UserSignInDTO signIn(
            UserSignInRequest userSignInRequest,
            CompletableFuture<Pair<String, String>> country
    ) {
        User user = getUserByEmail(userSignInRequest.email());
        validateIfPasswordsMatches(userSignInRequest, user);
        validateIfUserStateIsActive(user);
        String accessToken = jwtProvider.issueAccessToken(TokenIssuanceDTO.of(user));
        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        Pair<String, String> countryAndRegionPair = fetchLocation(country);

        return UserSignInDTO.of(
                user,
                accessToken,
                refreshToken,
                isLocationMatch(user, countryAndRegionPair)
        );
    }

    private static Pair<String, String> fetchLocation(
            CompletableFuture<Pair<String, String>> country
    ) {
        return country
                .orTimeout(5, TimeUnit.SECONDS) // 5초 이내에 완료되지 않으면 타임아웃
                .exceptionally(throwable -> Pair.of("Error", "Timeout"))
                .join();
    }

    private static boolean isLocationMatch(
            User user,
            Pair<String, String> countryPair
    ) {
        Location location = user.getLocation();
        String country = location.getCountry().getCountryName();
        String region = location.getRegion();
        return country.equals(countryPair.getFirst()) &&
                region.equals(countryPair.getSecond());
    }

    private static void validateIfUserStateIsActive(
            User user
    ) {
        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new CustomException(USER_STATUS_NOT_ACTIVE);
        }
    }

    private void validateIfPasswordsMatches(
            UserSignInRequest userSignInRequest,
            User user
    ) {
        if (!passwordEncoder.matches(userSignInRequest.password(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }
    }

    private User getUserByEmail(
            String email
    ) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

}
