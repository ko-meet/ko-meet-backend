package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@ApiModel(value = "UserInfoUpdateRequest", description = "사용자 정보 수정 요청 DTO")
public record UserInfoUpdateRequest(
        String nickName,
        String profileImage,
        Countries country,
        Countries interestCountry,
        Double latitude,
        Double longitude,
        UserStatus status
) {
}
