package com.backend.komeet.user.presentation.request;

import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.enums.UserStatus;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserInfoUpdateRequest", description = "사용자 정보 수정 요청 DTO")
public class UserInfoUpdateRequest {
    private String nickName;
    private String profileImage;
    private Countries country;
    private Countries interestCountry;
    private Double latitude;
    private Double longitude;
    private UserStatus status;
}
