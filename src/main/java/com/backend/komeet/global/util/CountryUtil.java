package com.backend.komeet.global.util;

import com.backend.komeet.global.exception.CustomException;
import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.komeet.global.exception.ErrorCode.LOCATION_NOT_MATCH;

@UtilityClass
public class CountryUtil {
    /**
     * 사용자의 위치 정보를 가져옴
     */
    public Pair<String, String> fetchLocation(
            CompletableFuture<Pair<String, String>> country
    ) {

        return country
                .orTimeout(5, TimeUnit.SECONDS) // 5초 이내에 완료되지 않으면 타임아웃
                .exceptionally(throwable -> Pair.of("Error", "Timeout"))
                .join();
    }

    /**
     * 도시 정보를 추출
     */
    public String extractCity(
            Pair<String, String> response,
            String country
    ) {
        if (!response.getFirst().equals(country)) {
            throw new CustomException(LOCATION_NOT_MATCH);
        }
        return response.getSecond();
    }
}
