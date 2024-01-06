package com.backend.komeet.dto;

import lombok.Builder;
import lombok.Getter;
import com.backend.komeet.domain.User;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.UserRole;

/**
 * 토큰 발급 DTO
 */
@Getter
@Builder
public class TokenIssuanceDto {
    private Long id;
    private String email;
    private UserRole userRole;
    private Countries country;

    public static TokenIssuanceDto from(User user) {
        return TokenIssuanceDto.builder()
                .id(user.getSeq())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .country(user.getCountry())
                .build();
    }
}
