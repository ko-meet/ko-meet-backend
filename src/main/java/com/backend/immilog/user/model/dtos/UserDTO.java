package com.backend.immilog.user.model.dtos;

import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.entities.User;
import lombok.*;

import java.sql.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long seq;
    private String nickName;
    private String email;
    private String profileImage;
    private Long reportedCount;
    private Date reportedDate;
    private Countries country;
    private Countries interestCountry;
    private String region;
    private UserRole userRole;
    private UserStatus userStatus;

    public static UserDTO toDTO(
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

    public static UserDTO toDTO(
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
