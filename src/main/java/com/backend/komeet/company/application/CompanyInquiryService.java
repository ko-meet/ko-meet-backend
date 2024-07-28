package com.backend.komeet.company.application;

import com.backend.komeet.company.model.dtos.CompanyDto;
import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.company.repositories.CompanyRepository;
import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyInquiryService {
    private final CompanyRepository companyRepository;

    /**
     * 단일 회사 정보 조회
     */
    public CompanyDto getCompany(
            Long userSeq
    ) {
        return CompanyDto.from(getCompanyEntity(userSeq));
    }

    /**
     * 회사 엔티티 조회
     */
    private Company getCompanyEntity(
            Long userSeq
    ) {
        return companyRepository
                .findByCompanyManagerUserSeq(userSeq)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));
    }
}
