package com.backend.komeet.service.post;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 게시물 업로드 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class PostUploadService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 게시물을 생성하는 메서드
     *
     * @param userId            사용자 식별자
     * @param postUploadRequest 게시물 생성 요청 데이터
     */
    public void uploadPost(Long userId, PostUploadRequest postUploadRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        postRepository.save(Post.from(postUploadRequest, user));
    }
}
