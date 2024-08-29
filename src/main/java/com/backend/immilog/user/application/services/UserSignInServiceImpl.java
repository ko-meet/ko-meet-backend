package com.backend.immilog.user.application.services;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.security.TokenProvider;
import com.backend.immilog.user.application.command.UserSignInCommand;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.model.repositories.UserRepository;
import com.backend.immilog.user.model.services.UserSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.immilog.user.exception.UserErrorCode.*;

@RequiredArgsConstructor
@Service
public class UserSignInServiceImpl implements UserSignInService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    @Override
    public UserSignInDTO signIn(
            UserSignInCommand userSignInCommand,
            CompletableFuture<Pair<String, String>> country
    ) {
        User user = getUser(userSignInCommand.email());
        validateIfPasswordsMatches(userSignInCommand, user);
        validateIfUserStateIsActive(user);
        String accessToken = tokenProvider.issueAccessToken(
                user.getSeq(),
                user.getEmail(),
                user.getUserRole(),
                user.getLocation().getCountry()
        );

        String refreshToken = tokenProvider.issueRefreshToken();

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

    @Override
    public UserSignInDTO getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    ) {
        final User user = getUser(userSeq);
        boolean isLocationMatch = isLocationMatch(user, country);
        final String accessToken = tokenProvider.issueAccessToken(
                user.getSeq(),
                user.getEmail(),
                user.getUserRole(),
                user.getLocation().getCountry()
        );
        final String refreshToken = tokenProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken,
                user.getEmail(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        return UserSignInDTO.of(
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
            UserSignInCommand userSignInCommand,
            User user
    ) {
        if (!passwordEncoder.matches(userSignInCommand.password(), user.getPassword())) {
            throw new CustomException(PASSWORD_NOT_MATCH);
        }
    }

    private User getUser(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private User getUser(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
