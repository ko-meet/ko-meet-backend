package com.backend.immilog.user.presentation.response;

import com.backend.immilog.user.model.enums.Countries;
import lombok.Builder;

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
