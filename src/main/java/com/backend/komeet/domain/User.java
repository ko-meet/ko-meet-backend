package com.backend.komeet.domain;

import lombok.*;
import com.backend.komeet.dto.request.UserSignUpRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.UserRole;
import com.backend.komeet.enums.UserStatus;
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

    public static User from(UserSignUpRequest userSignUpRequest,
                            String encodedPassword) {
        return User.builder()
                .nickName(userSignUpRequest.getNickName())
                .email(userSignUpRequest.getEmail())
                .password(encodedPassword)
                .country(Countries.getCountry(userSignUpRequest.getCountry()))
                .region(userSignUpRequest.getRegion())
                .userRole(UserRole.ROLE_USER)
                .userStatus(UserStatus.PENDING)
                .imageUrl(userSignUpRequest.getProfileImage())
                .build();
    }
}
