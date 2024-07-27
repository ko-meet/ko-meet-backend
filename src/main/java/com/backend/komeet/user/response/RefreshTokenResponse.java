package com.backend.komeet.user.response;

import lombok.*;
import org.springframework.data.util.Pair;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;

    public static RefreshTokenResponse from(
            Pair<String, String> tokens
    ) {
        return RefreshTokenResponse.builder()
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }
}
