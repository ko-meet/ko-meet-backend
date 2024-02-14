package com.backend.komeet.dto;

import com.backend.komeet.domain.Comment;
import com.backend.komeet.domain.Reply;
import com.backend.komeet.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private PostStatus status;

    public static CommentDto from(Comment comments) {
        return CommentDto.builder()
                .seq(comments.getSeq())
                .user(UserDto.from(comments.getUser()))
                .content(comments.getContent())
                .replies(
                        comments.getReplies()
                                .stream()
                                .map(ReplyDto::from)
                                .collect(Collectors.toList())
                )
                .upVotes(comments.getUpVotes())
                .downVotes(comments.getDownVotes())
                .status(comments.getStatus())
                .build();
    }
}
