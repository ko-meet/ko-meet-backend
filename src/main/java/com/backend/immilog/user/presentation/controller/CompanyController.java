package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.user.application.CompanyRegisterService;
import com.backend.immilog.user.presentation.request.CompanyRegisterRequest;
import com.backend.immilog.user.presentation.response.UserApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Company API", description = "회사정보 관련 API")
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@RestController
public class CompanyController {
    private final CompanyRegisterService companyRegisterService;

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
}
