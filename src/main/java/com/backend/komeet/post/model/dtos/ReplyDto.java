package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.model.entities.metadata.CommentMetaData;
import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.post.model.entities.Reply;
import com.backend.komeet.post.enums.PostStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 DTO
 */
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

    public static ReplyDto from(
            Reply reply
    ){
        CommentMetaData commentMetaData = reply.getCommentMetaData();
        return ReplyDto.builder()
                .seq(reply.getSeq())
                .user(UserDto.from(reply.getUser()))
                .content(commentMetaData.getContent())
                .upVotes(commentMetaData.getUpVotes())
                .downVotes(commentMetaData.getDownVotes())
                .likeUsers(commentMetaData.getLikeUsers())
                .status(commentMetaData.getStatus())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}
