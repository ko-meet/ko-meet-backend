package com.backend.immilog.post.infrastructure.result;

import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.domain.model.enums.PostStatus;
import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.user.application.result.UserInfoResult;
import com.backend.immilog.user.domain.model.User;
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
public class CommentEntityResult {
    private Long seq;
    private UserInfoResult user;
    private String content;
    private List<CommentEntityResult> replies;
    private int upVotes;
    private int downVotes;
    private int replyCount;
    private List<Long> likeUsers;
    private PostStatus status;
    private LocalDateTime createdAt;

    public CommentEntityResult(
            Comment comment,
            User user,
            List<Comment> replies,
            List<User> replyUsers
    ) {
        List<CommentEntityResult> replyList = combineReplies(replies, replyUsers);

        this.seq = comment.seq();
        this.user = UserInfoResult.from(user);
        this.content = comment.content();
        this.replies = replyList;
        this.upVotes = comment.likeCount();
        this.replyCount = comment.replyCount();
        this.likeUsers = comment.likeUsers();
        this.status = comment.status();
        this.createdAt = comment.createdAt();
    }

    public static CommentEntityResult of(
            Comment comment,
            User user
    ) {
        return CommentEntityResult.builder()
                .seq(comment.seq())
                .user(UserInfoResult.from(user))
                .content(comment.content())
                .replies(new ArrayList<>())
                .upVotes(comment.likeCount())
                .replyCount(comment.replyCount())
                .likeUsers(comment.likeUsers())
                .status(comment.status())
                .createdAt(comment.createdAt())
                .build();
    }

    private List<CommentEntityResult> combineReplies(
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
                            .filter(u -> u.seq().equals(reply.userSeq()))
                            .findFirst()
                            .map(replyUser -> CommentEntityResult.of(reply, replyUser))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public CommentResult toCommentResult() {
        return CommentResult.builder()
                .seq(seq)
                .user(user)
                .content(content)
                .replies(replies.stream().map(CommentEntityResult::toCommentResult).toList())
                .upVotes(upVotes)
                .downVotes(downVotes)
                .replyCount(replyCount)
                .likeUsers(likeUsers)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
