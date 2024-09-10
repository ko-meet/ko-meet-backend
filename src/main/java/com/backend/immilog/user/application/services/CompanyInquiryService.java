package com.backend.immilog.user.application.services;

import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.domain.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyInquiryService {
    private final CompanyRepository companyRepository;

    public CompanyResult getCompany(
            Long userSeq
    ) {
        return companyRepository.getByCompanyManagerUserSeq(userSeq)
                .map(CompanyResult::from)
                .orElse(CompanyResult.builder().build());
    }
}
