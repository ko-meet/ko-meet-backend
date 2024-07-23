package com.backend.komeet.company.presentation.request;

import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.user.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRegisterRequest {
    private Industry industry;
    private String companyName;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyHomepage;
    private Countries companyCountry;
    private String companyRegion;
    private String companyLogo;
}
