package com.backend.immilog.user.application.dto;

import com.backend.immilog.user.model.entities.User;
import lombok.Builder;

@Builder
public record UserSignInDTO(
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
    public static UserSignInDTO of(
            User user,
            String accessToken,
            String refreshToken,
            boolean isLocationMatch
    ) {
        String interestCountry =
                user.getInterestCountry() == null ? null :
                        user.getInterestCountry().getCountryName();

        return UserSignInDTO.builder()
                .userSeq(user.getSeq())
                .email(user.getEmail())
                .nickname(user.getNickName())
                .accessToken(accessToken == null ? "" : accessToken)
                .refreshToken(refreshToken == null ? "" : refreshToken)
                .country(user.getLocation().getCountry().getCountryName())
                .interestCountry(interestCountry)
                .region(user.getLocation().getRegion())
                .userProfileUrl(user.getImageUrl())
                .isLocationMatch(isLocationMatch)
                .build();
    }
}

