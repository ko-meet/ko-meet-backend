package com.backend.komeet.domain;

import com.backend.komeet.dto.request.JobBoardUploadRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.enums.PostStatus;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.enums.PostStatus.NORMAL;

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

    @ManyToOne(targetEntity = User.class)
    private User user;

    @Setter
    private Long viewCount;

    @Setter
    private Long likeCount;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> tags;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers;

    @ElementCollection
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
     *
     * @param postUploadRequest 게시물 업로드 요청 DTO
     * @param user              사용자
     * @return JobBoard
     */
    public static JobBoard from(JobBoardUploadRequest postUploadRequest,
                                User user) {
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
                .country(postUploadRequest.getCountry())
                .region(postUploadRequest.getRegion())
                .industry(postUploadRequest.getIndustry())
                .deadline(postUploadRequest.getDeadline())
                .experience(postUploadRequest.getExperience())
                .salary(postUploadRequest.getSalary())
                .company(postUploadRequest.getCompany())
                .companyEmail(postUploadRequest.getCompanyEmail())
                .companyPhone(postUploadRequest.getCompanyPhone())
                .companyAddress(postUploadRequest.getCompanyAddress())
                .companyHomepage(postUploadRequest.getCompanyHomepage())
                .companyLogo(postUploadRequest.getCompanyLogo())
                .status(NORMAL)
                .build();
    }
}
