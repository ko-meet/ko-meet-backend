package com.backend.komeet.service.jobboard;

import com.backend.komeet.post.application.jobboard.JobBoardSearchService;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("구인구직 게시판 검색 서비스 테스트")
class JobBoardSearchServiceTest {
    @Mock
    private JobBoardRepository jobBoardRepository;
    @Mock
    private UserRepository userRepository;
    private JobBoardSearchService jobBoardSearchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobBoardSearchService = new JobBoardSearchService(
                jobBoardRepository, userRepository
        );
    }

    JobBoard jobBoard = TestEntityGenerator.jobBoard;
    JobBoardDto jobBoardDto = JobBoardDto.from(jobBoard);

    @Test
    @DisplayName("구인구직 게시판 목록 조회 - 성공")
    void getJobBoards_Success() {
        // given
        String country = jobBoard.getPostMetaData().getCountry().toString();
        String sortingMethod = SortingMethods.VIEW_COUNT.name();
        String industry = Industry.ALL.getIndustry();
        String experience = Experience.ALL.getExperience();
        Integer page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<JobBoardDto> jobBoardDtos = new PageImpl<>(
                Collections.singletonList(jobBoardDto),
                pageRequest,
                1
        );

        when(jobBoardRepository
                .getJobBoards(country, sortingMethod, industry, experience, pageRequest))
                .thenReturn(jobBoardDtos);

        // when
        Page<JobBoardDto> jobBoards = jobBoardSearchService.getJobBoards(
                country, sortingMethod, industry, experience, page
        );

        // then
        assertThat(jobBoards.getContent().get(0)).isEqualTo(jobBoardDto);
    }

}