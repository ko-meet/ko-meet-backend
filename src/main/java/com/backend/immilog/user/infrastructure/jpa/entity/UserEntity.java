package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.model.vo.Location;
import com.backend.immilog.user.domain.model.vo.ReportInfo;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
@Table(name = "user")
public class UserEntity extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Setter
    private String nickName;

    private String email;

    @Setter
    private String password;

    @Setter
    private String imageUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserCountry interestCountry;

    @Embedded
    private Location location;

    @Embedded
    private ReportInfo reportInfo;

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .seq(user.seq())
                .nickName(user.nickName())
                .email(user.email())
                .password(user.password())
                .imageUrl(user.imageUrl())
                .userStatus(user.userStatus())
                .userRole(user.userRole())
                .interestCountry(user.interestCountry())
                .location(user.location())
                .reportInfo(user.reportInfo())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .seq(this.seq)
                .nickName(this.nickName)
                .email(this.email)
                .password(this.password)
                .imageUrl(this.imageUrl)
                .userStatus(this.userStatus)
                .userRole(this.userRole)
                .interestCountry(this.interestCountry)
                .location(this.location)
                .reportInfo(this.reportInfo)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}

