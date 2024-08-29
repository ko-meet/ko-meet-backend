package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.enums.Countries;
import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.user.model.services.LocationService;
import com.backend.immilog.user.presentation.response.LocationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.global.enums.Countries.SOUTH_KOREA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("LocationController 테스트")
class LocationControllerTest {
    @Mock
    private LocationService locationService;

    private LocationController locationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        locationController = new LocationController(locationService);
    }

    @Test
    @DisplayName("위치 정보 가져오기")
    void getLocation() {
        // given
        Double latitude = 37.5665;
        Double longitude = 126.9780;
        String country = "대한민국";
        String countryCode = "KR";

        Pair<String, String> countryPair = Pair.of(country, countryCode);

        when(locationService.getCountry(latitude, longitude))
                .thenReturn(CompletableFuture.completedFuture(countryPair));

        // when
        ResponseEntity<?> response = locationController.getLocation(latitude,
                longitude);

        // then
        assertThat(response).isNotNull();
        assertThat(OK).isEqualTo(response.getStatusCode());
        LocationResponse locationResponse =
                (LocationResponse) ((ApiResponse) Objects.requireNonNull(response.getBody())).data();
        assertThat(locationResponse).isNotNull();
        assertThat(Countries.getCountryByKoreanName(country).getCountryName())
                .isEqualTo(locationResponse.country());
        assertThat(SOUTH_KOREA.toString()).isEqualTo(locationResponse.country());
    }
}
