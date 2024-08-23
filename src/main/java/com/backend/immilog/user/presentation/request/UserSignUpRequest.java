package com.backend.immilog.user.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@ApiModel(value = "UserSignUpRequest", description = "사용자 회원가입 요청 DTO")
public record UserSignUpRequest(
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickName,

        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞게 입력해주세요.")
        String email,

        @NotNull(message = "국가를 입력해주세요.")
        String country,

        String interestCountry,

        String region,

        String profileImage
) {
}
