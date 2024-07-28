package com.backend.komeet.post.presentation.request;

import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.PostStatus;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 구인구직 게시물 업로드 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "JobBoardUploadRequest", description = "구인구직 게시물 업로드 요청 DTO")
public class JobBoardUploadRequest {
    private Long seq;
    private String title;
    private String content;
    private Long viewCount;
    private Long likeCount;
    private List<String> tags;
    private List<String> attachments;
    private LocalDateTime deadline;
    private Experience experience;
    private String salary;
    private Long companySeq;
    private PostStatus status;
}
