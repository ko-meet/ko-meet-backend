package com.backend.komeet.service.post;

import com.backend.komeet.domain.Post;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.PostCreateRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.PostRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public void createPost(Long userId, PostCreateRequest postCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        postRepository.save(Post.from(postCreateRequest, user));
    }
}
