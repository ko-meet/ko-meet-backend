package com.backend.immilog.user.application.command;

import lombok.Builder;

@Builder
public record UserSignUpCommand(
        String nickName,
        String password,
        String email,
        String country,
        String interestCountry,
        String region,
        String profileImage
) {
}
