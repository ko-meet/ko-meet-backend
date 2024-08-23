package com.backend.immilog.global.model;

import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.model.dtos.UserDTO;
import com.backend.immilog.user.model.entities.User;
import lombok.Builder;

@Builder
public record TokenIssuanceDTO(
        Long id,
        String email,
        UserRole userRole,
        Countries country
) {
    public static TokenIssuanceDTO of(
            UserDTO userDto
    ) {
        return TokenIssuanceDTO.builder()
                .id(userDto.seq())
                .email(userDto.email())
                .userRole(userDto.userRole())
                .country(userDto.country())
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
