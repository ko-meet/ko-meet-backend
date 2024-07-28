package com.backend.komeet.post.application.jobboard;

import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     */
    @Transactional(readOnly = true)
    public Page<JobBoardDto> getJobBoards(
            String country,
            String sortingMethod,
            String industry,
            String experience,
            Integer page
    ) {

        Pageable pageable = PageRequest.of(page, 10);

        return jobBoardRepository.getJobBoards(
                country,
                sortingMethod,
                industry,
                experience,
                pageable
        );
    }
}
