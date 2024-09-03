package com.backend.immilog.user.application.dto;

import com.backend.immilog.user.model.enums.UserCountry;
import com.backend.immilog.user.model.enums.UserStatus;
import lombok.Builder;

@Builder
public record UserInfoUpdateCommand(
        String nickName,
        String profileImage,
        UserCountry country,
        UserCountry interestCountry,
        Double latitude,
        Double longitude,
        UserStatus status
) {
}
