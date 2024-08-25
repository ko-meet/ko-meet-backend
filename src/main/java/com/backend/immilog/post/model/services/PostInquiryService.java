package com.backend.immilog.post.model.services;

import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.user.enums.Countries;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface PostInquiryService {
    @Transactional(readOnly = true)
    Page<PostDTO> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Integer page
    );

    @Transactional(readOnly = true)
    PostDTO getPost(Long postSeq);

    @Transactional(readOnly = true)
    Page<PostDTO> searchKeyword(
            String keyword,
            Integer page
    );

    @Transactional(readOnly = true)
    Page<PostDTO> getUserPosts(
            Long userSeq,
            Integer page
    );
}
