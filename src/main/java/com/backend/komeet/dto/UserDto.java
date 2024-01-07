package com.backend.komeet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.backend.komeet.domain.User;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.UserRole;
import com.backend.komeet.enums.UserStatus;

import java.sql.Date;

/**
 * 사용자 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long seq;
    private String nickName;
    private String email;
    private Long reportedCount;
    private Date reportedDate;
    private Countries country;
    private String region;
    private UserRole userRole;
    private UserStatus userStatus;

    public static UserDto from(User user) {
        return UserDto.builder()
                .seq(user.getSeq())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .reportedCount(user.getReportedCount())
                .reportedDate(user.getReportedDate())
                .country(user.getCountry())
                .region(user.getRegion())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .build();
    }
}