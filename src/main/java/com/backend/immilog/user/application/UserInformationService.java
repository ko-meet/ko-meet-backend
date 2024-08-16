package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.model.TokenIssuanceDTO;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.global.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    @Transactional(readOnly = true)
    public UserSignInDTO getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    ) {
        User user = getUser(userSeq);
        boolean isLocationMatch = isLocationMatch(user, country);

        String accessToken =
                jwtProvider.issueAccessToken(TokenIssuanceDTO.of(user));

        String refreshToken = jwtProvider.issueRefreshToken();

        redisService.saveKeyAndValue(
                TOKEN_PREFIX + refreshToken, user.getEmail(),
                REFRESH_TOKEN_EXPIRE_TIME
        );

        return UserSignInDTO.of(
                user,
                accessToken,
                refreshToken,
                isLocationMatch
        );
    }

    private User getUser(
            Long userSeq
    ) {
        return userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    private static boolean isLocationMatch(
            User user,
            Pair<String, String> countryPair
    ) {
        Countries country = user.getLocation().getCountry();
        String region = user.getLocation().getRegion();
        return country.getCountryKoreanName().equals(countryPair.getFirst()) &&
                region.equals(countryPair.getSecond());
    }
}