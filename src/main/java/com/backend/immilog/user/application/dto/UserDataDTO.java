package com.backend.immilog.user.application.dto;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.enums.UserCountry;
import com.backend.immilog.user.model.enums.UserStatus;
import lombok.Builder;

import java.sql.Date;

@Builder
public record UserDataDTO(
        Long seq,
        String nickName,
        String email,
        String profileImage,
        Long reportedCount,
        Date reportedDate,
        UserCountry country,
        UserCountry interestCountry,
        String region,
        UserRole userRole,
        UserStatus userStatus
) {
    public static UserDataDTO from(
            User user
    ) {
        return UserDataDTO.builder()
                .seq(user.getSeq())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .profileImage(user.getImageUrl())
                .reportedCount(user.getReportInfo().getReportedCount())
                .reportedDate(user.getReportInfo().getReportedDate())
                .country(user.getLocation().getCountry())
                .region(user.getLocation().getRegion())
                .interestCountry(user.getInterestCountry())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .build();
    }

    public static UserDataDTO from(
            Long seq,
            String nickName,
            String profileImage
    ) {
        return UserDataDTO.builder()
                .seq(seq)
                .nickName(nickName)
                .profileImage(profileImage)
                .build();
    }
}
