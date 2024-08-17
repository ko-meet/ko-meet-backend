package com.backend.immilog.user.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "UserPasswordChangeRequest", description = "사용자 비밀번호 변경 요청 DTO")
public class UserPasswordChangeRequest {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
    private String existingPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
    private String newPassword;
}