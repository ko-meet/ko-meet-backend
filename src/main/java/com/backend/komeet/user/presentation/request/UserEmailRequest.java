package com.backend.komeet.user.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 사용자 이메일 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserEmailRequest", description = "사용자 이메일 요청 DTO")
public class UserEmailRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;
}
