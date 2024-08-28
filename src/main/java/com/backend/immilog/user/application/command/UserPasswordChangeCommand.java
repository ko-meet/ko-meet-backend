package com.backend.immilog.user.application.command;

import lombok.Builder;

@Builder
public record UserPasswordChangeCommand(
        String existingPassword,
        String newPassword
) {
}