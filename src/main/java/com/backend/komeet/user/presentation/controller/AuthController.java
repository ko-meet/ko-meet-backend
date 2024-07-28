package com.backend.komeet.user.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.user.application.GeocoderService;
import com.backend.komeet.user.application.UserInformationService;
import com.backend.komeet.user.response.RefreshTokenResponse;
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
     */
    @GetMapping("/user")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보 조회 진행")
    public ResponseEntity<ApiResponse> getUser(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude
    ) {

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
     */
    @GetMapping("/refresh")
    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급 진행")
    public ResponseEntity<ApiResponse> refresh(
            @RequestParam("token") String refreshToken
    ) {
        Pair<String, String> newAccessToken =
                userInformationService.refreshToken(refreshToken);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(RefreshTokenResponse.from(newAccessToken)));
    }
}
