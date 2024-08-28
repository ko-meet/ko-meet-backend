package com.backend.immilog.global.model;

import com.backend.immilog.user.model.enums.Countries;
import com.backend.immilog.user.model.enums.UserRole;
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
            UserDTO userDTO
    ) {
        return TokenIssuanceDTO.builder()
                .id(userDTO.seq())
                .email(userDTO.email())
                .userRole(userDTO.userRole())
                .country(userDTO.country())
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
