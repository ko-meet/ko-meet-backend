package com.backend.komeet.dto.request;

import lombok.AllArgsConstructor;
import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.backend.komeet.enums.Countries;

import javax.validation.constraints.*;

/**
 * 사용자 회원가입 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignUpRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickName;

    @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @NotNull(message = "국가를 입력해주세요.")
    private String country;

    private String region;

    private String profileImage;
}
