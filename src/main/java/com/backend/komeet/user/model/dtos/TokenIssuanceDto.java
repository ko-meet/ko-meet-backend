package com.backend.komeet.user.model.dtos;

import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.enums.UserRole;
import lombok.*;
import com.backend.komeet.user.enums.Countries;

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

    public static TokenIssuanceDto from(
            User user
    ) {
        return TokenIssuanceDto.builder()
                .id(user.getSeq())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .country(user.getCountry())
                .build();
    }
}
