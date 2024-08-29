package com.backend.immilog.user.application.command;

import com.backend.immilog.global.enums.Countries;
import com.backend.immilog.user.model.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserInfoUpdateCommand(
        String nickName,
        String profileImage,
        Countries country,
        Countries interestCountry,
        Double latitude,
        Double longitude,
        UserStatus status
) {
}
