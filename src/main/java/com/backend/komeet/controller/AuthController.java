package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.dto.response.RefreshTokenResponse;
import com.backend.komeet.service.external.GeocoderService;
import com.backend.komeet.service.user.UserInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

/**
 * 인증 관련 컨트롤러
 */
@Api(tags = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final JwtProvider jwtProvider;
    private final GeocoderService geocoderService;
    private final UserInformationService userInformationService;

    /**
     * 사용자 정보 조회
     *
     * @param token     토큰
     * @param latitude  위도
     * @param longitude 경도
     * @return {@link ResponseEntity<ApiResponse>} 사용자 정보
     */
    @GetMapping("/user")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보 조회 진행")
    public ResponseEntity<ApiResponse> getUser(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        CompletableFuture<Pair<String, String>> country =
                geocoderService.getCountry(latitude, longitude);

        Long userSeq = jwtProvider.getIdFromToken(token);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(userInformationService.getUser(userSeq, country))
        );
    }

    /**
     * 토큰 재발급
     *
     * @param refreshToken 리프레시 토큰
     * @return {@link ResponseEntity<ApiResponse>} 토큰 재발급 결과
     */
    @GetMapping("/refresh")
    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급 진행")
    public ResponseEntity<ApiResponse> refresh(
            @RequestParam("token") String refreshToken) {
        Pair<String, String> newAccessToken =
                userInformationService.refreshToken(refreshToken);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(RefreshTokenResponse.from(newAccessToken)));
    }
}
