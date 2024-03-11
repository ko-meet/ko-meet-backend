package com.backend.komeet.controller;

import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.dto.response.LocationResponse;
import com.backend.komeet.service.external.GeocoderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Location API", description = "위치 관련 API")
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@RestController
public class LocationController {
    private final GeocoderService geocoderService;

    @GetMapping
    @ApiOperation(value = "위치 정보", notes = "위치 정보를 가져옵니다.")
    public ResponseEntity<ApiResponse> getLocation(@RequestParam Double latitude,
                                                   @RequestParam Double longitude) {

        CompletableFuture<Pair<String, String>> country =
                geocoderService.getCountry(latitude, longitude);

        LocationResponse response = LocationResponse.from(
                country.join().getFirst(), country.join().getSecond()
        );

        return ResponseEntity.status(OK).body(new ApiResponse(response));
    }
}
