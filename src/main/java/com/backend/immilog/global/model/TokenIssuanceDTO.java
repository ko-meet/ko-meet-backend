package com.backend.immilog.global.model;

import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.model.dtos.UserDTO;
import com.backend.immilog.user.model.entities.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenIssuanceDTO {
    private Long id;
    private String email;
    private UserRole userRole;
    private Countries country;

    public static TokenIssuanceDTO of(
            UserDTO userDto
    ) {
        return TokenIssuanceDTO.builder()
                .id(userDto.getSeq())
                .email(userDto.getEmail())
                .userRole(userDto.getUserRole())
                .country(userDto.getCountry())
                .build();
    }

    public static TokenIssuanceDTO of(
            User user
    ) {
        return TokenIssuanceDTO.builder()
                .id(user.getSeq())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .country(user.getLocation().getCountry())
                .build();
    }
}
