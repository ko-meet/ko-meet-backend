package com.backend.komeet.dto;

import lombok.*;
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
    private String profileImage;
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
                .profileImage(user.getImageUrl())
                .reportedCount(user.getReportedCount())
                .reportedDate(user.getReportedDate())
                .country(user.getCountry())
                .region(user.getRegion())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .build();
    }

    public static UserDto from(Long seq, String nickName, String profileImage){
        return UserDto.builder()
                .seq(seq)
                .nickName(nickName)
                .profileImage(profileImage)
                .build();
    }
}
