package com.backend.komeet.dto.request;

import com.backend.komeet.enums.Countries;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPasswordResetRequest {
    private String email;
    private Countries country;
}
