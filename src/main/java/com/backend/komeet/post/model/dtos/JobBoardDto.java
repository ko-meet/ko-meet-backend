package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.model.entities.metadata.CompanyMetaData;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.PostStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구인구직 게시판 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobBoardDto {
    private Long seq;
    private String title;
    private String content;
    private UserDto user;
    private Long viewCount;
    private Long likeCount;
    private List<String> tags;
    private List<String> attachments;
    private List<Long> likeUsers;
    private List<Long> bookmarkUsers;
    private Countries country;
    private String region;
    private Industry industry;
    private LocalDateTime deadline;
    private Experience experience;
    private String salary;
    private String company;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyHomepage;
    private String companyLogo;
    private PostStatus status;
    private LocalDateTime createdAt;

    /**
     * JobBoardDto 팩토리 메서드
     */
    public static JobBoardDto from(
            JobBoard jobBoard
    ) {
        PostMetaData postMetaData = jobBoard.getPostMetaData();
        CompanyMetaData companyMetaData = jobBoard.getCompanyMetaData();
        return JobBoardDto.builder()
                .seq(jobBoard.getSeq())
                .title(postMetaData.getTitle())
                .content(postMetaData.getContent())
                .user(UserDto.from(jobBoard.getUser()))
                .viewCount(postMetaData.getViewCount())
                .likeCount(postMetaData.getLikeCount())
                .tags(postMetaData.getTags())
                .attachments(postMetaData.getAttachments())
                .likeUsers(postMetaData.getLikeUsers())
                .bookmarkUsers(postMetaData.getBookmarkUsers())
                .country(postMetaData.getCountry())
                .region(postMetaData.getRegion())
                .industry(companyMetaData.getIndustry())
                .deadline(companyMetaData.getDeadline())
                .experience(companyMetaData.getExperience())
                .salary(companyMetaData.getSalary())
                .company(companyMetaData.getCompany())
                .companyEmail(companyMetaData.getCompanyEmail())
                .companyPhone(companyMetaData.getCompanyPhone())
                .companyAddress(companyMetaData.getCompanyAddress())
                .companyHomepage(companyMetaData.getCompanyHomepage())
                .companyLogo(companyMetaData.getCompanyLogo())
                .status(postMetaData.getStatus())
                .createdAt(jobBoard.getCreatedAt())
                .build();
    }
}
