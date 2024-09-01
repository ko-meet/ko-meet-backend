package com.backend.immilog.notice.model.dtos;

import com.backend.immilog.notice.model.entities.Notice;
import com.backend.immilog.notice.model.enums.Countries;
import com.backend.immilog.notice.model.enums.NoticeStatus;
import com.backend.immilog.notice.model.enums.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDTO {
    private Long seq;
    private Long authorUserSeq;
    private String title;
    private String content;
    private NoticeType type;
    private NoticeStatus status;
    private List<Countries> targetCountries;
    private List<Long> readUsers;
    private LocalDateTime createdAt;

    public static NoticeDTO from(
            Notice notice
    ) {
        String content = notice.getContent();
        if (content.length() > 30) {
            content = content.substring(0, 30);
        }
        return NoticeDTO.builder()
                .seq(notice.getSeq())
                .authorUserSeq(notice.getUserSeq())
                .title(notice.getTitle())
                .content(content + "...")
                .type(notice.getType())
                .status(notice.getStatus())
                .targetCountries(notice.getTargetCountries())
                .readUsers(notice.getReadUsers())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}

