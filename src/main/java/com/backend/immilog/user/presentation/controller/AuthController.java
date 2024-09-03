package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.user.application.services.LocationService;
import com.backend.immilog.user.application.services.UserSignInService;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Auth API", description = "인증 관련 API")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final LocationService locationService;
    private final UserSignInService userSignInService;

    @GetMapping("/user")
    @ExtractUserId
    @ApiOperation(value = "사용자 정보 조회", notes = "사용자 정보 조회 진행")
    public ResponseEntity<ApiResponse> getUser(
            HttpServletRequest request,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude
    ) {
        CompletableFuture<Pair<String, String>> countryFuture =
                locationService.getCountry(latitude, longitude);

        Pair<String, String> country =
                locationService.joinCompletableFutureLocation(countryFuture);

        Long userSeq = (Long) request.getAttribute("userSeq");

        UserSignInDTO userSignInDTO =
                userSignInService.getUserSignInDTO(userSeq, country);

        return ResponseEntity
                .status(OK)
                .body(ApiResponse.of(userSignInDTO));
    }

}

