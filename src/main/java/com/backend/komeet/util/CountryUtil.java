package com.backend.komeet.util;

import com.backend.komeet.exception.CustomException;
import lombok.experimental.UtilityClass;
import org.springframework.data.util.Pair;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.backend.komeet.exception.ErrorCode.LOCATION_NOT_MATCH;

@UtilityClass
public class CountryUtil {
    /**
     * 사용자의 위치 정보를 가져옴
     *
     * @param country CompletableFuture 객체
     * @return 나라와 도시 정보
     */
    public Pair<String, String> fetchLocation(CompletableFuture<Pair<String, String>> country) {

        return country
                .orTimeout(5, TimeUnit.SECONDS) // 5초 이내에 완료되지 않으면 타임아웃
                .exceptionally(throwable -> Pair.of("Error", "Timeout"))
                .join();
    }

    /**
     * 도시 정보를 추출
     *
     * @param response 외부 API 응답
     * @return 도시 정보
     */
    public String extractCity(Pair<String, String> response, String country) {
        if (!response.getFirst().equals(country)) {
            throw new CustomException(LOCATION_NOT_MATCH);
        }
        return response.getSecond();
    }
}
