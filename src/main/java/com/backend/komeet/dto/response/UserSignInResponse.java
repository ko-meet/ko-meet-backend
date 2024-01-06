package com.backend.komeet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.backend.komeet.dto.UserSignInDto;

/**
 * 사용자 로그인 응답 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInResponse {
    private String email;
    private String nickName;
    private String accessToken;
    private String refreshToken;
    private Boolean isLocationMatch;

    public static UserSignInResponse from(UserSignInDto userSignInDto) {
        return UserSignInResponse.builder()
                .email(userSignInDto.getEmail())
                .nickName(userSignInDto.getNickName())
                .accessToken(userSignInDto.getAccessToken())
                .refreshToken(userSignInDto.getRefreshToken())
                .isLocationMatch(userSignInDto.getIsLocationMatch())
                .build();
    }
}
