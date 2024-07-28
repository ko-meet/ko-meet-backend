package com.backend.komeet.post.application.post;

import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.repositories.PostRepository;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.infrastructure.exception.ErrorCode.*;

/**
 * 게시물 삭제 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class PostDeleteService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시물을 삭제하는 메서드
     */
    @Transactional
    public void deletePost(
            Long userId,
            Long postSeq
    ) {
        Post post = getPost(postSeq);
        User user = getUser(userId);
        if (!post.getUser().equals(user)) {
            throw new CustomException(NO_AUTHORITY);
        }
        post.setStatus(DELETED);
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     */
    private Post getPost(
            Long postSeq
    ) {
        Post post = postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
        if (post.getStatus().equals(DELETED)) {
            throw new CustomException(ALREADY_DELETED_POST);
        }
        return post;
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
