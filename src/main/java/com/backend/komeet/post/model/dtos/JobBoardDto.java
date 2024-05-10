package com.backend.komeet.post.model.dtos;

import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
     *
     * @param jobBoard {@link JobBoard}
     * @return {@link JobBoardDto}
     */
    public static JobBoardDto from(JobBoard jobBoard) {
        return JobBoardDto.builder()
                .seq(jobBoard.getSeq())
                .title(jobBoard.getTitle())
                .content(jobBoard.getContent())
                .user(UserDto.from(jobBoard.getUser()))
                .viewCount(jobBoard.getViewCount())
                .likeCount(jobBoard.getLikeCount())
                .tags(jobBoard.getTags())
                .attachments(jobBoard.getAttachments())
                .likeUsers(jobBoard.getLikeUsers())
                .bookmarkUsers(jobBoard.getBookmarkUsers())
                .country(jobBoard.getCountry())
                .region(jobBoard.getRegion())
                .industry(jobBoard.getIndustry())
                .deadline(jobBoard.getDeadline())
                .experience(jobBoard.getExperience())
                .salary(jobBoard.getSalary())
                .company(jobBoard.getCompany())
                .companyEmail(jobBoard.getCompanyEmail())
                .companyPhone(jobBoard.getCompanyPhone())
                .companyAddress(jobBoard.getCompanyAddress())
                .companyHomepage(jobBoard.getCompanyHomepage())
                .companyLogo(jobBoard.getCompanyLogo())
                .status(jobBoard.getStatus())
                .createdAt(jobBoard.getCreatedAt())
                .build();
    }
}
