package com.backend.immilog.post.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.post.model.services.PostInquiryService;
import com.backend.immilog.post.model.repositories.PostRepository;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.user.enums.Countries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.backend.immilog.global.exception.ErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostInquiryServiceImpl implements PostInquiryService {
    private final PostRepository postRepository;

    @Override
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

    @Override
    public PostDTO getPost(
            Long postSeq
    ) {
        return postRepository
                .getPost(postSeq)
                .orElseThrow(()-> new CustomException(POST_NOT_FOUND));
    }

    @Override
    public Page<PostDTO> searchKeyword(
            String keyword,
            Integer page
    ) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        return postRepository
                .getPostsByKeyword(keyword, pageRequest);
    }
}
