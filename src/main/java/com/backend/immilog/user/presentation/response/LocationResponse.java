package com.backend.immilog.user.presentation.response;

import com.backend.immilog.user.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record LocationResponse(
        String country,
        String region
) {
    public static LocationResponse from(
            Countries country,
            String region
    ) {
        return LocationResponse.builder()
                .country(country.getCountryName())
                .region(region)
                .build();
    }
}
