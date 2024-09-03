package com.backend.immilog.user.application.dto;

import lombok.Builder;

@Builder
public record UserSignInCommand(
        String email,
        String password,
        Double latitude,
        Double longitude
) {
}
