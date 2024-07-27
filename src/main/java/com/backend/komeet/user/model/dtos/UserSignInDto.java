package com.backend.komeet.user.model.dtos;

import com.backend.komeet.user.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 로그인 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInDto {
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

    /**
     * UserSignInDto 객체 생성
     */
    public static UserSignInDto from(
            User user,
            String accessToken,
            String refreshToken,
            boolean isLocationMatch
    ) {

        return UserSignInDto.builder()
                .userSeq(user.getSeq())
                .email(user.getEmail())
                .nickname(user.getNickName())
                .accessToken(accessToken == null ? "" : accessToken)
                .refreshToken(refreshToken == null ? "" : refreshToken)
                .country(user.getCountry().getCountryName())
                .interestCountry(
                        user.getInterestCountry() == null ?
                                null : user.getInterestCountry().getCountryName()
                )
                .region(user.getRegion())
                .userProfileUrl(user.getImageUrl())
                .isLocationMatch(isLocationMatch)
                .build();
    }
}
