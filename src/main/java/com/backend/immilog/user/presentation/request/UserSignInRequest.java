package com.backend.immilog.user.presentation.request;

import com.backend.immilog.user.application.dto.UserSignInCommand;
import io.swagger.annotations.ApiModel;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@ApiModel(value = "UserSignInRequest", description = "사용자 로그인 요청 DTO")
public record UserSignInRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞게 입력해주세요.")
        String email,

        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,

        Double latitude,

        Double longitude
) {
    public UserSignInCommand toCommand() {
        return UserSignInCommand.builder()
                .email(email)
                .password(password)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
