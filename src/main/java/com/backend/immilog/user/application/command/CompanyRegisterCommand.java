package com.backend.immilog.user.application.command;

import com.backend.immilog.user.domain.model.enums.Industry;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import lombok.Builder;

@Builder
public record CompanyRegisterCommand(
        Industry industry,
        String companyName,
        String companyEmail,
        String companyPhone,
        String companyAddress,
        String companyHomepage,
        UserCountry companyCountry,
        String companyRegion,
        String companyLogo
) {
}