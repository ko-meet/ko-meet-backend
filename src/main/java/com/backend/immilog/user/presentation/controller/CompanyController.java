package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.application.services.CompanyInquiryService;
import com.backend.immilog.user.application.services.CompanyRegisterService;
import com.backend.immilog.user.presentation.request.CompanyRegisterRequest;
import com.backend.immilog.user.presentation.response.UserApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Company API", description = "회사정보 관련 API")
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@RestController
public class CompanyController {
    private final CompanyRegisterService companyRegisterService;
    private final CompanyInquiryService companyInquiryService;

    @PostMapping
    @ExtractUserId
    @ApiOperation(value = "회사정보 등록", notes = "회사정보를 등록합니다.")
    public ResponseEntity<UserApiResponse> registerCompany(
            HttpServletRequest request,
            @RequestBody CompanyRegisterRequest param
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        companyRegisterService.registerOrEditCompany(userSeq, param.toCommand());

        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/my")
    @ExtractUserId
    @ApiOperation(value = "본인 회사정보 조회", notes = "본인 회사정보를 조회합니다.")
    public ResponseEntity<UserApiResponse> getCompany(
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        CompanyResult result = companyInquiryService.getCompany(userSeq);

        return ResponseEntity.status(OK).body(UserApiResponse.of(result));
    }
}
