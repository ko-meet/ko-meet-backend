package com.backend.komeet.dto;

import com.backend.komeet.domain.Reply;
import com.backend.komeet.enums.PostStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Long> likeUsers;
    private PostStatus status;
    private LocalDateTime createdAt;

    public static ReplyDto from(Reply reply){
        return ReplyDto.builder()
                .seq(reply.getSeq())
                .user(UserDto.from(reply.getAuthor()))
                .content(reply.getContent())
                .upVotes(reply.getUpVotes())
                .downVotes(reply.getDownVotes())
                .likeUsers(reply.getLikeUsers())
                .status(reply.getStatus())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}
