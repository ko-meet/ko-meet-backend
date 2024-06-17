package com.backend.komeet.service.jobboard;

import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.application.jobboard.JobBoardDetailService;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.repositories.JobBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.JOB_BOARD_NOT_FOUND;
import static com.backend.komeet.service.common.TestEntityGenerator.jobBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("구인구직 게시판 상세 조회 서비스 테스트")
class JobBoardDetailServiceTest {
    @Mock
    private JobBoardRepository jobBoardRepository;
    private JobBoardDetailService jobBoardDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobBoardDetailService = new JobBoardDetailService(jobBoardRepository);
    }

    JobBoardDto jobBoardDto = JobBoardDto.from(jobBoard);

    @Test
    @DisplayName("구인구직 게시판 상세 정보 조회 - 성공")
    void getJobBoardDetail_Success() {
        // given
        Long jobBoardSeq = 1L;
        when(jobBoardRepository.findById(jobBoardSeq)).thenReturn(Optional.of(jobBoard));
        // when
        JobBoardDto jobBoardDetail = jobBoardDetailService.getJobBoardDetail(jobBoardSeq);
        // then
        assertThat(jobBoardDetail.getCompany()).isEqualTo(jobBoardDto.getCompany());
        assertThat(jobBoardDetail.getSalary()).isEqualTo(jobBoardDto.getSalary());
    }

    @Test
    @DisplayName("구인구직 게시판 상세 정보 조회 - 실패")
    void getJobBoardDetail_Fail() {
        // given
        Long jobBoardSeq = 1L;
        when(jobBoardRepository.findById(jobBoardSeq)).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> jobBoardDetailService.getJobBoardDetail(jobBoardSeq))
                .isInstanceOf(CustomException.class)
                .hasMessage(JOB_BOARD_NOT_FOUND.getMessage());

    }
}