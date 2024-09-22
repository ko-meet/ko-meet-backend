package com.backend.immilog.post.application.result;

import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import com.backend.immilog.user.domain.model.enums.Industry;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record JobBoardResult(
        Long seq,
        String title,
        String content,
        Long viewCount,
        Long likeCount,
        List<String> tags,
        List<String> attachments,
        List<Long> likeUsers,
        List<Long> bookmarkUsers,
        Countries country,
        String region,
        Industry industry,
        LocalDateTime deadline,
        Experience experience,
        String salary,
        Long companySeq,
        String companyName,
        String companyEmail,
        String companyPhone,
        String companyAddress,
        String companyHomepage,
        String companyLogo,
        Long companyManagerUserSeq,
        PostStatus status,
        LocalDateTime createdAt
) {
}
