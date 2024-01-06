package com.backend.komeet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 로그인 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignInRequest {
    private String email;
    private String password;
}
