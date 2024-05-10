package com.backend.komeet.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;

    public static RefreshTokenResponse from(Pair<String, String> tokens) {
        return RefreshTokenResponse.builder()
                .accessToken(tokens.getFirst())
                .refreshToken(tokens.getSecond())
                .build();
    }
}
