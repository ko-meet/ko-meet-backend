package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Countries;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 비밀번호 재설정 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserPasswordResetRequest", description = "사용자 비밀번호 재설정 요청 DTO")
public class UserPasswordResetRequest {
    private String email;
    private Countries country;
}
