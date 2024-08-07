package com.backend.komeet.user.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.enums.UserRole;
import com.backend.komeet.user.enums.UserStatus;
import com.backend.komeet.user.presentation.request.UserSignUpRequest;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Date;

/**
 * User 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column
    @Setter
    private String nickName;

    @Column
    private String email;

    @Column
    @Setter
    private String password;

    @Column
    @Setter
    private Long reportedCount;

    @Column
    @Setter
    private Date reportedDate;

    @Column
    @Setter
    @Enumerated(EnumType.STRING)
    private Countries country;

    @Column
    @Setter
    private String region;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column
    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @Column
    @Setter
    private String imageUrl;

    @Column
    @Setter
    @Enumerated(EnumType.STRING)
    private Countries interestCountry;

    /**
     * User 팩토리 메서드
     */
    public static User from(
            UserSignUpRequest userSignUpRequest,
            String encodedPassword
    ) {
        Countries interestCountry =
                userSignUpRequest.getInterestCountry() != null ?
                        Countries.getCountry(userSignUpRequest.getInterestCountry()) : null;
        return User.builder()
                .nickName(userSignUpRequest.getNickName())
                .email(userSignUpRequest.getEmail())
                .password(encodedPassword)
                .country(Countries.getCountryByKoreanName(userSignUpRequest.getCountry()))
                .interestCountry(interestCountry)
                .region(userSignUpRequest.getRegion())
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.PENDING)
                .imageUrl(userSignUpRequest.getProfileImage())
                .build();
    }
}
