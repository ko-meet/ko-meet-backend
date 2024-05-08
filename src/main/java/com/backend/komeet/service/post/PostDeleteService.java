package com.backend.komeet.service.post;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.enums.PostStatus.DELETED;
import static com.backend.komeet.exception.ErrorCode.*;

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
     *
     * @param userId  사용자 식별자
     * @param postSeq 게시물 식별자
     */
    @Transactional
    public void deletePost(Long userId, Long postSeq) {
        Post post = getPost(postSeq);
        User user = getUser(userId);
        if (!post.getUser().equals(user)) {
            throw new CustomException(NO_AUTHORITY);
        }
        post.setStatus(DELETED);
    }

    /**
     * 게시물 식별자로 게시물을 조회하는 메서드
     *
     * @param postSeq 게시물 식별자
     * @return 게시물
     */
    private Post getPost(Long postSeq) {
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
     *
     * @param userId 사용자 식별자
     * @return 사용자
     */
    private User getUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
