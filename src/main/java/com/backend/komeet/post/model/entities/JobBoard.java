package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.model.entities.User;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.NORMAL;

/**
 * 게시물 엔티티
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Entity
public class JobBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Setter
    private String title;

    @Setter
    private String content;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    private User user;

    @Setter
    private Long viewCount;

    @Setter
    private Long likeCount;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> tags;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> bookmarkUsers;

    @Enumerated(EnumType.STRING)
    private Countries country;

    private String region;

    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Setter
    private LocalDateTime deadline;

    @Setter
    private Experience experience;

    @Setter
    private String salary;

    @Setter
    private String company;

    @Setter
    private String companyEmail;

    @Setter
    private String companyPhone;

    @Setter
    private String companyAddress;

    @Setter
    private String companyHomepage;

    @Setter
    private String companyLogo;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    /**
     * 게시물 팩토리 메서드
     */
    public static JobBoard from(
            JobBoardUploadRequest postUploadRequest,
            Company company,
            User user
    ) {
        return JobBoard.builder()
                .title(postUploadRequest.getTitle())
                .content(postUploadRequest.getContent())
                .user(user)
                .viewCount(0L)
                .likeCount(0L)
                .tags(postUploadRequest.getTags())
                .attachments(postUploadRequest.getAttachments())
                .likeUsers(new ArrayList<>())
                .bookmarkUsers(new ArrayList<>())
                .country(company.getCompanyCountry())
                .region(company.getCompanyRegion())
                .industry(company.getIndustry())
                .deadline(postUploadRequest.getDeadline())
                .experience(postUploadRequest.getExperience())
                .salary(postUploadRequest.getSalary())
                .company(company.getCompanyName())
                .companyEmail(company.getCompanyEmail())
                .companyPhone(company.getCompanyPhone())
                .companyAddress(company.getCompanyAddress())
                .companyHomepage(company.getCompanyHomepage())
                .companyLogo(company.getCompanyLogo())
                .status(NORMAL)
                .build();
    }
}
