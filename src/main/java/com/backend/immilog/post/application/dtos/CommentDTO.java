package com.backend.immilog.post.application.dtos;

import com.backend.immilog.post.model.entities.Comment;
import com.backend.immilog.post.model.enums.PostStatus;
import com.backend.immilog.user.application.dto.UserDataDTO;
import com.backend.immilog.user.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {
    private Long seq;
    private UserDataDTO user;
    private String content;
    private List<CommentDTO> replies;
    private int upVotes;
    private int downVotes;
    private int replyCount;
    private List<Long> likeUsers;
    private PostStatus status;
    private LocalDateTime createdAt;

    public CommentDTO(
            Comment comment,
            User user,
            List<Comment> replies,
            List<User> replyUsers
    ) {
        List<CommentDTO> replyList = combineReplies(replies, replyUsers);

        this.seq = comment.getSeq();
        this.user = UserDataDTO.from(user);
        this.content = comment.getContent();
        this.replies = replyList;
        this.upVotes = comment.getLikeCount();
        this.replyCount = comment.getReplyCount();
        this.likeUsers = comment.getLikeUsers();
        this.status = comment.getStatus();
        this.createdAt = comment.getCreatedAt();
    }

    public static CommentDTO of(
            Comment comment,
            User user
    ) {
        return CommentDTO.builder()
                .seq(comment.getSeq())
                .user(UserDataDTO.from(user))
                .content(comment.getContent())
                .replies(new ArrayList<>())
                .upVotes(comment.getLikeCount())
                .replyCount(comment.getReplyCount())
                .likeUsers(comment.getLikeUsers())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private List<CommentDTO> combineReplies(
            List<Comment> replies,
            List<User> replyUsers
    ) {
        if (replies.isEmpty() || replyUsers.isEmpty()) {
            return List.of();
        }
        return replies
                .stream()
                .map(reply -> {
                    return replyUsers.stream()
                            .filter(Objects::nonNull)
                            .filter(u -> u.getSeq().equals(reply.getUserSeq()))
                            .findFirst()
                            .map(replyUser -> CommentDTO.of(reply, replyUser))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
