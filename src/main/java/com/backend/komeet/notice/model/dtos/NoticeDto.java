package com.backend.komeet.notice.model.dtos;

import com.backend.komeet.notice.enums.NoticeType;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.enums.Countries;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDto {
    private Long seq;
    private Long authorUserSeq;
    private String title;
    private String content;
    private NoticeType type;
    private PostStatus status;
    private List<Countries> targetCountries;
    private List<Long> readUsers;

    public static NoticeDto from(Notice notice) {
        return NoticeDto.builder()
                .seq(notice.getSeq())
                .authorUserSeq(notice.getAuthorUserSeq())
                .title(notice.getTitle())
                .content(notice.getContent())
                .type(notice.getType())
                .status(notice.getStatus())
                .targetCountries(notice.getTargetCountries())
                .readUsers(notice.getReadUsers())
                .build();
    }
}