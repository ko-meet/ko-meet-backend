package com.backend.immilog.user.application.services;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.security.TokenProvider;
import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.application.result.UserSignInResult;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.model.vo.Location;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.immilog.user.exception.UserErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserSignInService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    @Transactional(readOnly = true)
    public UserSignInResult signIn(
            UserSignInCommand userSignInCommand,
            CompletableFuture<Pair<String, String>> country
    ) {
        User user = getUser(userSignInCommand.email());
        validateIfPasswordsMatches(userSignInCommand, user);
        validateIfUserStateIsActive(user);
        String accessToken = tokenProvider.issueAccessToken(
                user.seq(),
                user.email(),
                user.userRole(),
                user.location().getCountry().toGlobalCountries()
        );

        String refreshToken = tokenProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.email(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        Pair<String, String> countryAndRegionPair = fetchLocation(country);

        return UserSignInResult.of(
                user,
                accessToken,
                refreshToken,
                isLocationMatch(user, countryAndRegionPair)
        );
    }

    @Transactional(readOnly = true)
    public UserSignInResult getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    ) {
        final User user = getUser(userSeq);
        boolean isLocationMatch = isLocationMatch(user, country);
        final String accessToken = tokenProvider.issueAccessToken(
                user.seq(),
                user.email(),
                user.userRole(),
                user.location().getCountry().toGlobalCountries()
        );
        final String refreshToken = tokenProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken,
                user.email(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        return UserSignInResult.of(
                user,
                accessToken,
                refreshToken,
                isLocationMatch
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
        Location location = user.location();
        String country = location.getCountry().getCountryName();
        String region = location.getRegion();
        return country.equals(countryPair.getFirst()) &&
                region.equals(countryPair.getSecond());
    }

    private static void validateIfUserStateIsActive(
            User user
    ) {
        if (!user.userStatus().equals(UserStatus.ACTIVE)) {
            throw new UserException(USER_STATUS_NOT_ACTIVE);
        }
    }

    private void validateIfPasswordsMatches(
            UserSignInCommand userSignInCommand,
            User user
    ) {
        if (!passwordEncoder.matches(userSignInCommand.password(), user.password())) {
            throw new UserException(PASSWORD_NOT_MATCH);
        }
    }

    private User getUser(String email) {
        return userRepository
                .getByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    private User getUser(Long id) {
        return userRepository
                .getById(id)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
