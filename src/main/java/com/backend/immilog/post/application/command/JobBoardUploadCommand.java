package com.backend.immilog.post.application.command;

import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record JobBoardUploadCommand(
        Long seq,
        String title,
        String content,
        Long viewCount,
        Long likeCount,
        List<String> tags,
        List<String> attachments,
        LocalDateTime deadline,
        Experience experience,
        String salary,
        Long companySeq,
        PostStatus status
) {
}
