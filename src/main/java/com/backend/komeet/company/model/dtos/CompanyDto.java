package com.backend.komeet.company.model.dtos;

import com.backend.komeet.company.model.entities.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회사 정보 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDto {
    private Long seq;
    private String industry;
    private String company;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyHomepage;
    private String companyCountry;
    private String companyLogo;
    private String companyCountryCode;
    private String companyIndustryCode;
    private Long companyManagerUserSeq;

    public static CompanyDto from(Company company){
        return CompanyDto.builder()
                .seq(company.getSeq())
                .industry(company.getIndustry().getIndustry())
                .company(company.getCompanyName())
                .companyEmail(company.getCompanyEmail())
                .companyPhone(company.getCompanyPhone())
                .companyAddress(company.getCompanyAddress())
                .companyHomepage(company.getCompanyHomepage())
                .companyLogo(company.getCompanyLogo())
                .companyCountry(company.getCompanyCountry().getCountryName())
                .companyManagerUserSeq(company.getCompanyManagerUserSeq())
                .companyCountryCode(company.getCompanyCountry().getCountryName())
                .companyIndustryCode(company.getIndustry().toString())
                .build();
    }
}
