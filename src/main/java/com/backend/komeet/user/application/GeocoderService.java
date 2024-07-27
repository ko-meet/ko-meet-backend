package com.backend.komeet.user.application;

import com.backend.komeet.user.enums.Countries;
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
import java.util.Objects;
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
     */
    @Async
    public CompletableFuture<Pair<String, String>> getCountry(
            Double latitude,
            Double longitude
    ) {
        if (latitude <= 0.0 || longitude <= 0.0) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            CompletableFuture<String> response = generateWebClient(latitude, longitude);
            log.info(String.format(geocoderUrl, latitude, longitude, geocoderKey));
            String compoundCode = extractCompoundCode(response.join());
            String[] parts = Objects.requireNonNull(compoundCode).split(" ");
            if (parts.length >= 3) {
                String country = Countries.getCountryByKoreanName(parts[1]).getCountryName();
                String city = parts[2];
                return CompletableFuture.completedFuture(Pair.of(country, city));
            }
        } catch (Exception e) {
            log.error("Geocoder API 호출 중 예외 발생", e);
        }
        return CompletableFuture.completedFuture(Pair.of("기타 국가", "기타 지역"));
    }

    private CompletableFuture<String> generateWebClient(
            Double latitude,
            Double longitude
    ) {
        return webClient.build()
                .get()
                .uri(String.format(geocoderUrl, latitude, longitude, geocoderKey))
                .header("Accept-Language", "ko-KR")
                .retrieve()
                .bodyToMono(String.class)
                .toFuture();
    }

    /**
     * JSON 문자열에서 compound_code 키의 값을 추출
     */
    private String extractCompoundCode(
            String jsonResponse
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode plusCodeNode = rootNode.path("plus_code");
            if (!plusCodeNode.isMissingNode()) {
                return plusCodeNode.path("compound_code").asText();
            }
        } catch (IOException e) {
            log.error("JSON 파싱 중 예외 발생", e);
        }
        return null;
    }
}
