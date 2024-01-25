package com.backend.komeet.service.comment;

import com.backend.komeet.domain.Comment;
import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.CommentUploadRequest;
import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.enums.PostStatus;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.CommentRepository;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.enums.PostStatus.*;
import static com.backend.komeet.exception.ErrorCode.POST_NOT_FOUND;
import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
     * @param userId 사용자 식별자
     * @param postSeq 게시물 식별자
     * @param commentUploadRequest 댓글 업로드 요청 데이터
     */
    @Transactional
    public void uploadComment(Long userId,
                              Long postSeq,
                              CommentUploadRequest commentUploadRequest){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        Post post = postRepository.findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        Comment comment = commentRepository.save(
                Comment.from(user, post, commentUploadRequest.getContent())
        );

        post.getComments().add(comment);
    }
}
