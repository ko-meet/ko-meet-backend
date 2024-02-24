package com.backend.komeet.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 사용자 로그인 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자여야 합니다.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    private Double latitude;
    private Double longitude;
}
