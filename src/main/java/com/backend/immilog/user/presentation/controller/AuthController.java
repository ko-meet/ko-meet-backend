package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.application.LocationService;
import com.backend.immilog.user.application.UserInformationService;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final JwtProvider jwtProvider;
    private final LocationService locationService;
    private final UserInformationService userInformationService;

    @GetMapping("/user")
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보 조회 진행")
    public ResponseEntity<ApiResponse> getUser(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude
    ) {
        CompletableFuture<Pair<String, String>> countryFuture =
                locationService.getCountry(latitude, longitude);

        Pair<String, String> country =
                locationService.joinCompletableFutureLocation(countryFuture);

        Long userSeq = jwtProvider.getIdFromToken(token);

        UserSignInDTO userSignInDTO =
                userInformationService.getUserSignInDTO(userSeq, country);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(userSignInDTO));
    }

}

