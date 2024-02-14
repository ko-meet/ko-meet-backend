package com.backend.komeet.dto;

import com.backend.komeet.domain.Reply;
import com.backend.komeet.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyDto {
    private Long seq;
    private UserDto user;
    private String content;
    private int upVotes;
    private int downVotes;
    private PostStatus status;
    private LocalDateTime createdAt;

    public static ReplyDto from(Reply reply){
        return ReplyDto.builder()
                .seq(reply.getSeq())
                .user(UserDto.from(reply.getAuthor()))
                .content(reply.getContent())
                .upVotes(reply.getUpVotes())
                .downVotes(reply.getDownVotes())
                .status(reply.getStatus())
                .build();
    }
}
