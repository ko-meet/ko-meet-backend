package com.backend.immilog.user.application.dto;

import lombok.Builder;

@Builder
public record UserPasswordChangeCommand(
        String existingPassword,
        String newPassword
) {
}