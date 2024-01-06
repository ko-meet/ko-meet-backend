package com.backend.komeet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.backend.komeet.domain.User;

/**
 * 사용자 로그인 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInDto {
    private String email;
    private String nickName;
    private String accessToken;
    private String refreshToken;
    private Boolean isLocationMatch;

    /**
     * UserSignInDto 객체 생성
     * @param user
     * @param accessToken
     * @param refreshToken
     * @param isLocationMatch
     * @return
     */
    public static UserSignInDto from(User user,
                                     String accessToken,
                                     String refreshToken,
                                     boolean isLocationMatch) {

        return UserSignInDto.builder()
                .email(user.getEmail())
                .nickName(user.getNickName())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isLocationMatch(isLocationMatch)
                .build();
    }
}
