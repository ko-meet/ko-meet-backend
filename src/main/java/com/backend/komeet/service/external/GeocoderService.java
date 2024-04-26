package com.backend.komeet.service.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Geocoder API를 사용하여 위도와 경도를 기반으로 국가와 도시를 추출하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GeocoderService {
    private final WebClient.Builder webClient;

    @Value("${geocode.url}")
    private String geocoderUrl;

    @Value("${geocode.key}")
    private String geocoderKey;

    /**
     * 위도와 경도를 기반으로 국가와 도시를 추출
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return 국가와 도시
     */
    @Async
    public CompletableFuture<Pair<String, String>> getCountry(Double latitude, Double longitude) {
        try {
            CompletableFuture<String> response =
                    webClient.build()
                            .get()
                            .uri(String.format(geocoderUrl, latitude, longitude, geocoderKey))
                            .header("Accept-Language", "ko-KR")
                            .retrieve()
                            .bodyToMono(String.class)
                            .toFuture();
            log.info(String.format(geocoderUrl, latitude, longitude, geocoderKey));

            // join()를 사용하여 CompletableFuture의 결과를 동기적으로 가져옴
            String compoundCode = extractCompoundCode(response.join());

            String[] parts = compoundCode.split(" ");
            if (parts.length >= 3) {
                String country = parts[1];
                String city = parts[2];
                return CompletableFuture.completedFuture(Pair.of(country, city));
            }
        } catch (Exception e) {
            log.error("Geocoder API 호출 중 예외 발생", e);
        }

        return CompletableFuture.completedFuture(Pair.of("기타 국가", "기타 지역"));
    }

    /**
     * JSON 문자열에서 compound_code 키의 값을 추출
     *
     * @param jsonResponse JSON 문자열
     * @return compound_code 키의 값
     */
    private String extractCompoundCode(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 JsonNode로 읽어들임
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // plus_code의 compound_code 값을 추출
            JsonNode plusCodeNode = rootNode.path("plus_code");
            if (!plusCodeNode.isMissingNode()) {
                return plusCodeNode.path("compound_code").asText();
            }
        } catch (IOException e) {
            log.error("JSON 파싱 중 예외 발생", e);
        }

        return null; // 값을 찾지 못한 경우 null 반환
    }
}
