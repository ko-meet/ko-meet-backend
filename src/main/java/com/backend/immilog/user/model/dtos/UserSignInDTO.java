package com.backend.immilog.user.model.dtos;

import com.backend.immilog.user.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInDTO {
    private Long userSeq;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;
    private String country;
    private String interestCountry;
    private String region;
    private String userProfileUrl;
    private Boolean isLocationMatch;

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

