package com.backend.komeet.service.jobboard;

import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.repository.JobBoardRepository;
import com.backend.komeet.repository.UserRepository;
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

    public Page<JobBoardDto> getJobBoards(String country, String sortingMethod, Industry industry, Experience experience, Integer page, Long userSeq) {
        Pageable pageable = PageRequest.of(page, 10);

        return null;
    }
}
