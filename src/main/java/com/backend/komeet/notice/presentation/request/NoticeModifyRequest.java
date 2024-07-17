package com.backend.komeet.notice.presentation.request;

import com.backend.komeet.notice.enums.NoticeType;
import com.backend.komeet.post.enums.PostStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeModifyRequest {
    private String title;
    private String content;
    private NoticeType type;
    private PostStatus status;
}
