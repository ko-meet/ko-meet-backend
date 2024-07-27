package com.backend.komeet.company.application;

import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.company.presentation.request.CompanyRegisterRequest;
import com.backend.komeet.company.repositories.CompanyRepository;
import com.backend.komeet.user.enums.Countries;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class CompanyRegisterService {
    private final CompanyRepository companyRepository;

    /**
     * 회사 정보를 등록하거나 수정합니다.
     */
    @Transactional
    public void registerOrEditCompany(
            Long userSeq,
            CompanyRegisterRequest request
    ) {
        AtomicBoolean isExistingCompany = new AtomicBoolean(false);
        checkIfExistingAndProceedWithUpdate(userSeq, request, isExistingCompany);
        if (!isExistingCompany.get()) {
            createCompany(userSeq, request);
        }
    }

    /**
     * 회사 정보가 이미 존재하는지 확인하고 수정을 진행합니다.
     */
    private void checkIfExistingAndProceedWithUpdate(
            Long userSeq,
            CompanyRegisterRequest request,
            AtomicBoolean isExistingCompany
    ) {
        companyRepository.findByCompanyManagerUserSeq(userSeq)
                .ifPresent(company -> {
                    updateCompany(company, request);
                    isExistingCompany.set(true);
                });
    }

    /**
     * 회사 정보를 생성합니다.
     */
    private void createCompany(
            Long userSeq,
            CompanyRegisterRequest request
    ) {
        companyRepository.save(Company.from(userSeq, request));
    }

    /**
     * 회사 정보를 수정합니다.
     */
    private void updateCompany(
            Company company,
            CompanyRegisterRequest request
    ) {
        updateCompanyAddressIfItsNotNull(company, request.getCompanyAddress());
        updateCompanyCountryIfItsNotNull(company, request.getCompanyCountry());
        updateCompanyEmailIfItsNotNull(company, request.getCompanyEmail());
        updateCompanyHomepageIfItsNotNull(company, request.getCompanyHomepage());
        updateCompanyLogoIfItsNotNull(company, request.getCompanyLogo());
        updateCompanyPhoneIfItsNotNull(company, request.getCompanyPhone());
        updateCompanyCountryNameIfItsNotNull(company, request.getCompanyName());
        updateCompanyRegionIfItsNotNull(company, request.getCompanyRegion());
        updateIndustryIfItsNotNull(company, request);
    }

    /**
     * 회사 정보의 산업 정보를 수정합니다.
     */
    private static void updateIndustryIfItsNotNull(
            Company company,
            CompanyRegisterRequest request
    ) {
        if (request.getIndustry() != null) {
            company.setIndustry(request.getIndustry());
        }
    }

    /**
     * 회사 정보의 회사 이름을 수정합니다.
     */
    private static void updateCompanyCountryNameIfItsNotNull(
            Company company,
            String companyName
    ) {
        if (companyName != null) {
            company.setCompanyName(companyName);
        }
    }

    /**
     * 회사 정보의 회사 전화번호를 수정합니다.
     */
    private static void updateCompanyPhoneIfItsNotNull(
            Company company,
            String companyPhone
    ) {
        if (companyPhone != null) {
            company.setCompanyPhone(companyPhone);
        }
    }

    /**
     * 회사 정보의 회사 로고를 수정합니다.
     */
    private static void updateCompanyLogoIfItsNotNull(
            Company company,
            String companyLogo
    ) {
        if (companyLogo != null) {
            company.setCompanyLogo(companyLogo);
        }
    }

    /**
     * 회사 정보의 회사 홈페이지를 수정합니다.
     */
    private static void updateCompanyHomepageIfItsNotNull(
            Company company,
            String companyHomepage
    ) {
        if (companyHomepage != null) {
            company.setCompanyHomepage(companyHomepage);
        }
    }

    /**
     * 회사 정보의 회사 이메일을 수정합니다.
     */
    private static void updateCompanyEmailIfItsNotNull(
            Company company,
            String companyEmail
    ) {
        if (companyEmail != null) {
            company.setCompanyEmail(companyEmail);
        }
    }

    /**
     * 회사 정보의 회사 국가를 수정합니다.
     */
    private static void updateCompanyCountryIfItsNotNull(
            Company company,
            Countries country
    ) {
        if (country != null) {
            company.setCompanyCountry(country);
        }
    }

    /**
     * 회사 정보의 회사 주소를 수정합니다.
     */
    private static void updateCompanyAddressIfItsNotNull(
            Company company,
            String address
    ) {
        if (address != null) {
            company.setCompanyAddress(address);
        }
    }

    /**
     * 회사 정보의 회사 주소를 수정합니다.
     */
    private static void updateCompanyRegionIfItsNotNull(
            Company company,
            String region
    ) {
        if (region != null) {
            company.setCompanyRegion(region);
        }
    }
}
