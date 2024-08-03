package com.backend.komeet.post.application.comment;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.model.entities.Comment;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.presentation.request.CommentUploadRequest;
import com.backend.komeet.post.repositories.CommentRepository;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.global.exception.ErrorCode.POST_NOT_FOUND;
import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 댓글 업로드 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class CommentUploadService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 댓글을 업로드하는 메서드
     */
    @Transactional
    public void uploadComment(
            Long userId,
            Long postSeq,
            CommentUploadRequest commentUploadRequest
    ) {
        User user = getUser(userId);
        Post post = getPost(postSeq);
        post.getComments().add(getComment(commentUploadRequest, user, post));
        post.setCommentCount(post.getCommentCount() + 1);
    }

    /**
     * 댓글 엔티티를 생성하는 메서드
     */
    private Comment getComment(
            CommentUploadRequest commentUploadRequest,
            User user,
            Post post
    ) {
        return commentRepository.save(
                Comment.from(user, post, commentUploadRequest.getContent())
        );
    }

    /**
     * 게시물 엔티티를 조회하는 메서드
     */
    private Post getPost(
            Long postSeq
    ) {
        return postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    /**
     * 사용자 엔티티를 조회하는 메서드
     */
    private User getUser(
            Long userId
    ) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
