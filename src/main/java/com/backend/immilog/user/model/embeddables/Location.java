package com.backend.immilog.user.model.embeddables;

import com.backend.immilog.global.enums.Countries;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Location {
    @Enumerated(EnumType.STRING)
    private Countries country;
    private String region;

    public static Location of(
            Countries country,
            String region
    ) {
        return Location.builder()
                .country(country)
                .region(region)
                .build();
    }
}
