package com.backend.komeet.post.application;

import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 구인구직 게시판 검색 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JobBoardSearchService {
    private final JobBoardRepository jobBoardRepository;
    private final UserRepository userRepository;

    /**
     * 구인구직 게시판 목록을 조회하는 메서드
     *
     * @param country       국가
     * @param sortingMethod {@link SortingMethods} 정렬 방식
     * @param industry      {@link Industry} 업종
     * @param experience    {@link Experience} 경력
     * @param page          페이지 정보
     * @return {@link Page<JobBoardDto>} 구인구직 게시판 목록
     */
    public Page<JobBoardDto> getJobBoards(String country,
                                          String sortingMethod,
                                          String industry,
                                          String experience,
                                          Integer page) {

        Pageable pageable = PageRequest.of(page, 10);

        return jobBoardRepository
                .getJobBoards(country, sortingMethod, industry, experience, pageable);
    }
}
