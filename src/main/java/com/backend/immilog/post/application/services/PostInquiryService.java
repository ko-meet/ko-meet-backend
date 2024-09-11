package com.backend.immilog.post.application.services;

import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.application.result.CommentResult;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.domain.model.enums.Categories;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.SortingMethods;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.domain.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backend.immilog.post.exception.PostErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostInquiryService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<PostResult> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 10);

        return postRepository.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public PostResult getPost(
            Long postSeq
    ) {
        PostResult postResult = getPostDTO(postSeq);
        List<CommentResult> comments = commentRepository.getComments(postSeq);
        postResult = postResult.copyWithNewComments(comments);
        return postResult;
    }

    @Transactional(readOnly = true)
    public Page<PostResult> searchKeyword(
            String keyword,
            Integer page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return postRepository
                .getPostsByKeyword(keyword, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getUserPosts(
            Long userSeq,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository
                .getPostsByUserSeq(userSeq, pageable);
    }

    private PostResult getPostDTO(Long postSeq) {
        return postRepository
                .getPost(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
