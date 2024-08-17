package com.backend.immilog.user.application;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.model.TokenIssuanceDTO;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserInfoUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.backend.immilog.global.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInformationService {
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final ImageService imageService;

    final int REFRESH_TOKEN_EXPIRE_TIME = 5 * 29 * 24 * 60;
    final String TOKEN_PREFIX = "Refresh: ";

    @Transactional(readOnly = true)
    public UserSignInDTO getUserSignInDTO(
            Long userSeq,
            Pair<String, String> country
    ) {
        final User user = getUser(userSeq);
        boolean isLocationMatch = isLocationMatch(user, country);
        TokenIssuanceDTO tokenTokenIssuanceDto = TokenIssuanceDTO.of(user);
        final String accessToken = jwtProvider.issueAccessToken(tokenTokenIssuanceDto);
        final String refreshToken = jwtProvider.issueRefreshToken();

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

    @Transactional
    public void updateInformation(
            Long userSeq,
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateRequest userInfoUpdateRequest
    ) {
        User user = getUser(userSeq);
        setUserCountryIfItsChanged(country, userInfoUpdateRequest, user);
        setUserNickNameIfItsChanged(userInfoUpdateRequest.getNickName(), user);
        setUserInterestCountryIfItsChanged(userInfoUpdateRequest.getInterestCountry(), user);
        setUserStatusIfItsChanged(userInfoUpdateRequest.getStatus(), user);
        setUserImageProfileIfItsChanged(userInfoUpdateRequest.getProfileImage(), user);
    }

    private static void setUserCountryIfItsChanged(
            CompletableFuture<Pair<String, String>> country,
            UserInfoUpdateRequest userInfoUpdateRequest,
            User user
    ) {
        try {
            if (userInfoUpdateRequest.getCountry() != null && country.get() != null) {
                Pair<String, String> countryPair = country.join();
                user.getLocation().setRegion(countryPair.getSecond());
                user.getLocation().setCountry(userInfoUpdateRequest.getCountry());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }

    private static void setUserNickNameIfItsChanged(
            String userNickName,
            User user
    ) {
        if (userNickName != null) {
            user.setNickName(userNickName);
        }
    }

    private static void setUserInterestCountryIfItsChanged(
            Countries interestCountry,
            User user
    ) {
        if (interestCountry != null) {
            user.setInterestCountry(interestCountry);
        }
    }

    private static void setUserStatusIfItsChanged(
            UserStatus status,
            User user
    ) {
        if (status != null) {
            user.setUserStatus(status);
        }
    }

    private void setUserImageProfileIfItsChanged(
            String profileUrl,
            User user
    ) {
        if (profileUrl != null) {
            imageService.deleteFile(user.getImageUrl());
            user.setImageUrl(profileUrl.isEmpty() ? null : profileUrl);
        }
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