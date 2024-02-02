package com.backend.komeet.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 국가 코드
 */
@Getter
@RequiredArgsConstructor
public enum Countries {
    ALL("전체","ALL"),
    MALAYSIA("말레이시아","MY"),
    SINGAPORE("싱가포르","SG"),
    INDONESIA("인도네시아","ID"),
    VIETNAM("베트남","VN"),
    PHILIPPINES("필리핀","PH"),
    THAILAND("태국","TH"),
    MYANMAR("미얀마","MM"),
    CAMBODIA("캄보디아","KH"),
    LAOS("라오스","LA"),
    BRUNEI("브루나이","BN"),
    EAST_TIMOR("동티모르","TL"),
    CHINA("중국","CN"),
    JAPAN("일본","JP"),
    SOUTH_KOREA("대한민국","KR"),
    AUSTRALIA("오스트레일리아","AU"),
    NEW_ZEALAND("뉴질랜드","NZ"),
    GUAM("괌","GU"),
    SAI_PAN("사이판","MP"),
    ECT("기타국가","ETC");

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
