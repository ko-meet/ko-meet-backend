package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.dtos.JobBoardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 구인구직 게시판 레포지토리
 */
public interface JobBoardQRepository {
    Page<JobBoardDto> getJobBoards(
            String country,
            String sortingMethod,
            String industry,
            String experience,
            Pageable page
    );
}
