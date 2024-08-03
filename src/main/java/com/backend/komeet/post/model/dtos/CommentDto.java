package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.model.entities.Comment;
import com.backend.komeet.user.model.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long seq;
    private UserDto user;
    private String content;
    private List<ReplyDto> replies = new ArrayList<>();
    private int upVotes;
    private int downVotes;
    private int replyCount;
    private List<Long> likeUsers;
    private PostStatus status;
    private LocalDateTime createdAt;

    public static CommentDto from(
            Comment comments
    ) {
        return CommentDto.builder()
                .seq(comments.getSeq())
                .user(UserDto.from(comments.getUser()))
                .content(comments.getContent())
                .replies(comments.getReplies().stream().map(ReplyDto::from).toList())
                .replyCount(comments.getReplyCount())
                .upVotes(comments.getUpVotes())
                .downVotes(comments.getDownVotes())
                .likeUsers(comments.getLikeUsers())
                .status(comments.getStatus())
                .createdAt(comments.getCreatedAt())
                .build();
    }
}
