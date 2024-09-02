package com.backend.immilog.user.application.services;

import com.backend.immilog.global.enums.GlobalCountry;
import com.backend.immilog.user.model.services.LocationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Geocoder API를 사용하여 위도와 경도를 기반으로 국가와 도시를 추출하는 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {
    private final RestTemplate restTemplate;

    @Value("${geocode.url}")
    private String geocoderUrl;

    @Value("${geocode.key}")
    private String geocoderKey;

    @Override
    public CompletableFuture<Pair<String, String>> getCountry(
            Double latitude,
            Double longitude
    ) {
        if (latitude <= 0.0 || longitude <= 0.0) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            CompletableFuture<String> response = generateRestTemplate(latitude, longitude);
            log.info(String.format(geocoderUrl, latitude, longitude, geocoderKey));
            String compoundCode = extractCompoundCode(response.join());
            String[] parts = Objects.requireNonNull(compoundCode).split(" ");
            if (parts.length >= 3) {
                String country = GlobalCountry.getCountryByKoreanName(parts[1]).getCountryName();
                String city = parts[2];
                return CompletableFuture.completedFuture(Pair.of(country, city));
            }
        } catch (Exception e) {
            log.error("Geocoder API 호출 중 예외 발생", e);
        }
        return CompletableFuture.completedFuture(Pair.of("기타 국가", "기타 지역"));
    }

    private CompletableFuture<String> generateRestTemplate(
            Double latitude,
            Double longitude
    ) {
        return CompletableFuture.supplyAsync(() -> {
            String url = String.format(geocoderUrl, latitude, longitude, geocoderKey);
            return restTemplate.getForObject(url, String.class);
        });
    }

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

    @Override
    public Pair<String, String> joinCompletableFutureLocation(
            CompletableFuture<Pair<String, String>> countryFuture
    ) {
        return countryFuture
                .orTimeout(5, TimeUnit.SECONDS) // 5초 이내에 완료되지 않으면 타임아웃
                .exceptionally(throwable -> Pair.of("Error", "Timeout"))
                .join();
    }
}
