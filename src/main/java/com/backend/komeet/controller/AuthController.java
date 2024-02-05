package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.UserSignInDto;
import com.backend.komeet.dto.response.ApiResponse;
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
    @GetMapping("/user")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보 조회 진행")
    public ResponseEntity<ApiResponse> getUser(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude) {

        CompletableFuture<Pair<String, String>> country =
                geocoderService.getCountry(latitude, longitude);

        Long userSeq = jwtProvider.getIdFromToken(token);

        UserSignInDto userSignInDto =
                userInformationService.getUser(userSeq,country);

        return ResponseEntity.status(OK).body(new ApiResponse(userSignInDto));
    }

    @GetMapping("/refresh")
    @ApiOperation(value = "토큰 재발급", notes = "토큰 재발급 진행")
    public ResponseEntity<ApiResponse> refresh(
            @RequestParam("token") String refreshToken) {
        String newAccessToken =
                userInformationService.refreshToken(refreshToken);
        return ResponseEntity.status(OK).body(new ApiResponse(newAccessToken));
    }
}
