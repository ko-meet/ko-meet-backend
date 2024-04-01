package com.backend.komeet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 국가 코드
 */
@Getter
@RequiredArgsConstructor
public enum Countries {
    ALL("ALL","ALL"),
    MALAYSIA("MALAYSIA","MY"),
    SINGAPORE("SINGAPORE","SG"),
    INDONESIA("INDONESIA","ID"),
    VIETNAM("VIETNAM","VN"),
    PHILIPPINES("PHILIPPINES","PH"),
    THAILAND("THAILAND","TH"),
    MYANMAR("MYANMAR","MM"),
    CAMBODIA("CAMBODIA","KH"),
    LAOS("LAOS","LA"),
    BRUNEI("BRUNEI","BN"),
    EAST_TIMOR("EAST_TIMOR","TL"),
    CHINA("CHINA","CN"),
    JAPAN("JAPAN","JP"),
    SOUTH_KOREA("SOUTH_KOREA","KR"),
    AUSTRALIA("AUSTRALIA","AU"),
    NEW_ZEALAND("NEW_ZEALAND","NZ"),
    GUAM("GUAM","GU"),
    SAI_PAN("SAI_PAN","MP"),
    ECT("ECT","ETC");

    private final String countryName;
    private final String countryCode;

    public static Countries getCountry(String countryName) {
        for (Countries country : Countries.values()) {
            if (country.getCountryName().equals(countryName)) {
                return country;
            }
        }
        return null;
    }
}
