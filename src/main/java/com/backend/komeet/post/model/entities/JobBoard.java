package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.post.model.entities.metadata.CompanyMetaData;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.user.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;

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

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    private User user;

    @Embedded
    private PostMetaData postMetaData;

    @Embedded
    private CompanyMetaData companyMetaData;

    /**
     * 게시물 팩토리 메서드
     */
    public static JobBoard from(
            JobBoardUploadRequest postUploadRequest,
            Company company,
            User user
    ) {
        return JobBoard.builder()
                .postMetaData(
                        PostMetaData.builder()
                                .title(postUploadRequest.getTitle())
                                .content(postUploadRequest.getContent())
                                .viewCount(0L)
                                .likeCount(0L)
                                .tags(postUploadRequest.getTags())
                                .attachments(postUploadRequest.getAttachments())
                                .likeUsers(new ArrayList<>())
                                .bookmarkUsers(new ArrayList<>())
                                .country(company.getCompanyCountry())
                                .region(company.getCompanyRegion())
                                .status(NORMAL)
                                .build()
                )
                .companyMetaData(
                        CompanyMetaData.builder()
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
                                .build()
                )
                .user(user)
                .build();
    }
}
