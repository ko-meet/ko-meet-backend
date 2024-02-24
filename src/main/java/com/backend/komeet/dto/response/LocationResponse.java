package com.backend.komeet.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private String country;
    private String region;

    public static LocationResponse from(String country, String region) {
        return LocationResponse.builder()
                .country(country)
                .region(region)
                .build();
    }
}
