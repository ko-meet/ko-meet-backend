package com.backend.komeet.user.response;

import com.backend.komeet.user.enums.Countries;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private String country;
    private String region;

    public static LocationResponse from(Countries country, String region) {
        return LocationResponse.builder()
                .country(country.getCountryName())
                .region(region)
                .build();
    }
}
