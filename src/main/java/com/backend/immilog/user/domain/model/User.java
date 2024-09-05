package com.backend.immilog.user.domain.model;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.model.vo.Location;
import com.backend.immilog.user.domain.model.vo.ReportInfo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record User(
        Long seq,
        String nickName,
        String email,
        String password,
        String imageUrl,
        UserStatus userStatus,
        UserRole userRole,
        UserCountry interestCountry,
        Location location,
        ReportInfo reportInfo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static User of(
            UserSignUpCommand userSignUpCommand,
            String encodedPassword
    ) {
        String countryString = userSignUpCommand.interestCountry();
        UserCountry interestCountry = countryString != null ? UserCountry.getCountry(countryString) : null;
        UserCountry country = UserCountry.getCountryByKoreanName(userSignUpCommand.country());

        return User.builder()
                .nickName(userSignUpCommand.nickName())
                .email(userSignUpCommand.email())
                .password(encodedPassword)
                .location(Location.of(country, userSignUpCommand.region()))
                .interestCountry(interestCountry)
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.PENDING)
                .imageUrl(userSignUpCommand.profileImage())
                .build();
    }

    public User copyWithNewPassword(
            String encodedPassword
    ) {
        return buildUser(encodedPassword, "password");
    }

    public User copyWithNewUserStatus(
            UserStatus userStatus
    ) {
        return buildUser(userStatus, "userStatus");
    }

    public User copyWithNewNickName(
            String userNickName
    ) {
        return buildUser(userNickName, "nickName");
    }

    public User copyWithNewInterestCountry(
            UserCountry interestCountry
    ) {
        return buildUser(interestCountry, "interestCountry");
    }

    public User copyWithNewImageUrl(
            String imageUrl
    ) {
        return buildUser(imageUrl, "imageUrl");
    }

    private <T> User buildUser(
            T fieldValue,
            String fieldName
    ) {
        boolean isPassword = fieldName.equals("password");
        boolean isImageUrl = fieldName.equals("imageUrl");
        boolean isInterestCountry = fieldName.equals("interestCountry");
        boolean isNickName = fieldName.equals("nickName");
        boolean isUserStatus = fieldName.equals("userStatus");

        return User.builder()
                .nickName(isNickName ? (String) fieldValue : this.nickName)
                .password(isPassword ? (String) fieldValue : this.password)
                .imageUrl(isImageUrl ? (String) fieldValue : this.imageUrl)
                .interestCountry(isInterestCountry ? (UserCountry) fieldValue : this.interestCountry)
                .seq(this.seq)
                .email(this.email)
                .userStatus(isUserStatus ? (UserStatus) fieldValue : this.userStatus)
                .userRole(this.userRole)
                .location(this.location)
                .reportInfo(this.reportInfo)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
