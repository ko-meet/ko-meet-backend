package com.backend.immilog.post.application.services;

import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.application.dtos.CommentDTO;
import com.backend.immilog.post.application.dtos.PostDTO;
import com.backend.immilog.post.model.enums.Categories;
import com.backend.immilog.post.model.enums.Countries;
import com.backend.immilog.post.model.enums.SortingMethods;
import com.backend.immilog.post.model.repositories.CommentRepository;
import com.backend.immilog.post.model.repositories.PostRepository;
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
    public Page<PostDTO> getPosts(
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
    public PostDTO getPost(
            Long postSeq
    ) {
        PostDTO postDTO = getPostDTO(postSeq);
        List<CommentDTO> comments = commentRepository.getComments(postSeq);
        postDTO.setComments(comments);
        return postDTO;
    }

    @Transactional(readOnly = true)
    public Page<PostDTO> searchKeyword(
            String keyword,
            Integer page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return postRepository
                .getPostsByKeyword(keyword, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<PostDTO> getUserPosts(
            Long userSeq,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        return postRepository
                .getPostsByUserSeq(userSeq, pageable);
    }

    private PostDTO getPostDTO(Long postSeq) {
        return postRepository
                .getPost(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
