package com.backend.immilog.user.application.result;

import com.backend.immilog.user.domain.model.User;
import lombok.Builder;

@Builder
public record UserSignInResult(
        Long userSeq,
        String email,
        String nickname,
        String accessToken,
        String refreshToken,
        String country,
        String interestCountry,
        String region,
        String userProfileUrl,
        Boolean isLocationMatch
) {
    public static UserSignInResult of(
            User user,
            String accessToken,
            String refreshToken,
            boolean isLocationMatch
    ) {
        String interestCountry =
                user.interestCountry() == null ? null :
                        user.interestCountry().getCountryName();

        return UserSignInResult.builder()
                .userSeq(user.seq())
                .email(user.email())
                .nickname(user.nickName())
                .accessToken(accessToken == null ? "" : accessToken)
                .refreshToken(refreshToken == null ? "" : refreshToken)
                .country(user.location().getCountry().getCountryName())
                .interestCountry(interestCountry)
                .region(user.location().getRegion())
                .userProfileUrl(user.imageUrl())
                .isLocationMatch(isLocationMatch)
                .build();
    }
}

