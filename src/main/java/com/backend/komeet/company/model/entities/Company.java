package com.backend.komeet.company.model.entities;

import com.backend.komeet.company.presentation.request.CompanyRegisterRequest;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.user.enums.Countries;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 회사 정보 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Setter
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Setter
    private String companyName;

    @Setter
    private String companyEmail;

    @Setter
    private String companyPhone;

    @Setter
    private String companyAddress;

    @Setter
    private String companyHomepage;

    @Setter
    private Countries companyCountry;

    @Setter
    private String companyRegion;

    @Setter
    private String companyLogo;

    @Setter
    private Long companyManagerUserSeq;

    public static Company from(
            Long userSeq,
            CompanyRegisterRequest request
    ) {
        return Company.builder()
                .industry(request.getIndustry())
                .companyName(request.getCompanyName())
                .companyEmail(request.getCompanyEmail())
                .companyPhone(request.getCompanyPhone())
                .companyAddress(request.getCompanyAddress())
                .companyHomepage(request.getCompanyHomepage())
                .companyCountry(request.getCompanyCountry())
                .companyRegion(request.getCompanyRegion())
                .companyLogo(request.getCompanyLogo())
                .companyManagerUserSeq(userSeq)
                .build();
    }
}

