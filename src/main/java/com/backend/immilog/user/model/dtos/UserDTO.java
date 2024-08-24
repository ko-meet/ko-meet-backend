package com.backend.immilog.user.model.dtos;

import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.entities.User;
import lombok.Builder;

import java.sql.Date;

@Builder
public record UserDTO(
        Long seq,
        String nickName,
        String email,
        String profileImage,
        Long reportedCount,
        Date reportedDate,
        Countries country,
        Countries interestCountry,
        String region,
        UserRole userRole,
        UserStatus userStatus
) {
    public static UserDTO from(
            User user
    ) {
        return UserDTO.builder()
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

    public static UserDTO from(
            Long seq,
            String nickName,
            String profileImage
    ) {
        return UserDTO.builder()
                .seq(seq)
                .nickName(nickName)
                .profileImage(profileImage)
                .build();
    }
}
