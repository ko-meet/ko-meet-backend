package com.backend.komeet.service.jobboard;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.enums.SortingMethods;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.JobBoardRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
     * @param userSeq       사용자 식별자
     * @return {@link Page<JobBoardDto>} 구인구직 게시판 목록
     */
    public Page<JobBoardDto> getJobBoards(String country,
                                          SortingMethods sortingMethod,
                                          Industry industry,
                                          Experience experience,
                                          Integer page,
                                          Long userSeq) {

        User user = userRepository.findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 10);

        return jobBoardRepository.getJobBoards(
                country, sortingMethod, industry, experience, pageable, userSeq
        );
    }
}
