package com.backend.komeet.post.application.reply;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.model.entities.Comment;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.entities.Reply;
import com.backend.komeet.post.presentation.request.CommentUploadRequest;
import com.backend.komeet.post.repositories.CommentRepository;
import com.backend.komeet.post.repositories.ReplyRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.COMMENT_NOT_FOUND;
import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 대댓글 업로드 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReplyUploadService {
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 대댓글을 업로드하는 메서드
     */
    @Transactional
    public void uploadComment(
            Long userId,
            Long commentSeq,
            CommentUploadRequest commentUploadRequest
    ) {
        User user = getUser(userId);
        Comment comment = getComment(commentSeq);
        comment.getReplies().add(getReply(commentUploadRequest, user, comment));
        comment.setReplyCount(comment.getReplyCount() + 1);
        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() + 1);
    }

    /**
     * 대댓글 엔티티를 생성하는 메서드
     */
    private Reply getReply(
            CommentUploadRequest commentUploadRequest,
            User user,
            Comment comment
    ) {
        return replyRepository.save(
                Reply.from(user, comment, commentUploadRequest.getContent())
        );
    }

    /**
     * 댓글 식별자로 댓글을 조회하는 메서드
     */
    private Comment getComment(
            Long commentSeq
    ) {
        return commentRepository
                .getComment(commentSeq)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
    }

    /**
     * 사용자 식별자로 사용자를 조회하는 메서드
     */
    private User getUser(
            Long userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

}
