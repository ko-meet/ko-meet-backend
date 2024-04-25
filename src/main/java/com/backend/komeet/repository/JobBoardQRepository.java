package com.backend.komeet.repository;

import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.enums.SortingMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 구인구직 게시판 레포지토리
 */
public interface JobBoardQRepository {
    Page<JobBoardDto> getJobBoards(String country,
                                   String sortingMethod,
                                   String industry,
                                   String experience,
                                   Pageable page);
}
