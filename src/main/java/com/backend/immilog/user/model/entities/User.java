package com.backend.immilog.user.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.embeddables.ReportInfo;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
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
            UserSignUpRequest userSignUpRequest,
            String encodedPassword
    ) {
        Countries interestCountry =
                userSignUpRequest.interestCountry() != null ?
                        Countries.getCountry(userSignUpRequest.interestCountry()) : null;
        Countries country = Countries.getCountryByKoreanName(userSignUpRequest.country());

        return User.builder()
                .nickName(userSignUpRequest.nickName())
                .email(userSignUpRequest.email())
                .password(encodedPassword)
                .location(
                        Location.of(
                                country,
                                userSignUpRequest.region()
                        )
                )
                .interestCountry(interestCountry)
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.PENDING)
                .imageUrl(userSignUpRequest.profileImage())
                .build();
    }
}
