package com.backend.immilog.user.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.user.application.command.UserSignUpCommand;
import com.backend.immilog.user.model.enums.Countries;
import com.backend.immilog.user.model.enums.UserRole;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.embeddables.ReportInfo;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class User extends BaseDateEntity {

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
    private Countries interestCountry;

    @Embedded
    private Location location;

    @Embedded
    private ReportInfo reportInfo;

    public static User of(
            UserSignUpCommand userSignUpCommand,
            String encodedPassword
    ) {
        Countries interestCountry =
                userSignUpCommand.interestCountry() != null ?
                        Countries.getCountry(userSignUpCommand.interestCountry()) : null;
        Countries country = Countries.getCountryByKoreanName(userSignUpCommand.country());

        return User.builder()
                .nickName(userSignUpCommand.nickName())
                .email(userSignUpCommand.email())
                .password(encodedPassword)
                .location(
                        Location.of(
                                country,
                                userSignUpCommand.region()
                        )
                )
                .interestCountry(interestCountry)
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.PENDING)
                .imageUrl(userSignUpCommand.profileImage())
                .build();
    }
}
