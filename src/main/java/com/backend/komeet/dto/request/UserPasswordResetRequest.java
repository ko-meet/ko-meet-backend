package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPasswordResetRequest {
    private String email;
    private Countries country;
}
