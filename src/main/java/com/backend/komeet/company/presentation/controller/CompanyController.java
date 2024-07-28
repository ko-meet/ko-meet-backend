package com.backend.komeet.company.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.company.application.CompanyInquiryService;
import com.backend.komeet.company.application.CompanyRegisterService;
import com.backend.komeet.company.model.dtos.CompanyDto;
import com.backend.komeet.company.presentation.request.CompanyRegisterRequest;
import com.backend.komeet.global.security.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

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
    private final CompanyInquiryService companyInquiryService;

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

    @GetMapping("/my")
    @ApiOperation(value = "본인 회사정보 조회", notes = "본인 회사정보를 조회합니다.")
    public ResponseEntity<ApiResponse> getCompany(
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        CompanyDto companyDto = companyInquiryService.getCompany(userSeq);

        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(companyDto));
    }
}


