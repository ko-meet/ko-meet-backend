package com.backend.immilog.post.application.services;

import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.model.entities.Post;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.repositories.PostResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.backend.immilog.post.exception.PostErrorCode.NO_AUTHORITY;
import static com.backend.immilog.post.exception.PostErrorCode.POST_NOT_FOUND;
import static com.backend.immilog.post.model.enums.PostStatus.DELETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostDeleteService {
    private final PostRepository postRepository;
    private final PostResourceRepository postResourceRepository;

    @Transactional
    public void deletePost(
            Long userId,
            Long postSeq
    ) {
        Post post = getPost(postSeq);
        validateAuthor(userId, post);
        deletePostAndResources(post);
    }

    private void deletePostAndResources(
            Post post
    ) {
        post.getPostMetaData().setStatus(DELETED);
        postResourceRepository.deleteAllByPostSeq(post.getSeq());
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository.findById(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    private void validateAuthor(
            Long userId,
            Post post
    ) {
        if (!Objects.equals(post.getPostUserData().getUserSeq(), userId)) {
            throw new PostException(NO_AUTHORITY);
        }
    }
}