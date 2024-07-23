package com.backend.komeet.company.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.company.application.CompanyRegisterService;
import com.backend.komeet.company.presentation.request.CompanyRegisterRequest;
import com.backend.komeet.infrastructure.security.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * 회사정보 관련 컨트롤러
 */
@Api(tags = "Company API", description = "회사정보 관련 API")
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@RestController
public class CompanyController {
    private final JwtProvider jwtProvider;
    private final CompanyRegisterService companyRegisterService;

    @PostMapping
    @ApiOperation(value = "회사정보 등록", notes = "회사정보를 등록합니다.")
    public ResponseEntity<ApiResponse> registerCompany(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody CompanyRegisterRequest request
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        companyRegisterService.registerOrEditCompany(userSeq, request);

        return ResponseEntity
                .status(CREATED)
                .build();
    }
}


