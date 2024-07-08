package com.backend.komeet.user.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.response.LocationResponse;
import com.backend.komeet.user.application.GeocoderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

/**
 * 위치 관련 컨트롤러
 */
@Api(tags = "Location API", description = "위치 관련 API")
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@RestController
public class LocationController {
    private final GeocoderService geocoderService;

    /**
     * 위치 정보
     *
     * @param latitude  위도
     * @param longitude 경도
     * @return {@link ResponseEntity<ApiResponse>} 위치 정보
     */
    @GetMapping
    @ApiOperation(value = "위치 정보", notes = "위치 정보를 가져옵니다.")
    public ResponseEntity<ApiResponse> getLocation(@RequestParam Double latitude,
                                                   @RequestParam Double longitude) {
        Pair<String, String> country =
                geocoderService.getCountry(latitude, longitude).join();

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(
                        LocationResponse.from(
                                Countries.getCountryByKoreanName(country.getFirst()),
                                country.getSecond()))
                );
    }
}
