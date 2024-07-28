package com.backend.komeet.service.jobboard;

import com.backend.komeet.company.repositories.CompanyRepository;
import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.application.jobboard.JobBoardUploadService;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.service.common.TestEntityGenerator;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.backend.komeet.global.exception.ErrorCode.USER_INFO_NOT_FOUND;
import static com.backend.komeet.service.common.TestRequestGenerator.createJobBoardUploadRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("구인구직 게시판 업로드 서비스 테스트")
public class JobBoardUploadServiceTest {

    @Mock
    private JobBoardRepository jobBoardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    private JobBoardUploadService jobBoardUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobBoardUploadService = new JobBoardUploadService(
                jobBoardRepository, userRepository, companyRepository
        );
    }

    JobBoardUploadRequest jobBoardUploadRequest = createJobBoardUploadRequest();
    User user = TestEntityGenerator.user;
    JobBoard jobBoard = TestEntityGenerator.jobBoard;
    JobBoardDto jobBoardDto = JobBoardDto.from(jobBoard);

    @Test
    @DisplayName("구인구직 게시글 업로드 - 성공")
    void testPostJobBoard_Success() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(jobBoardRepository.save(any(JobBoard.class))).thenReturn(jobBoard);
        when(companyRepository.findById(any(Long.class))).thenReturn(Optional.of(TestEntityGenerator.company));

        // when
        JobBoardDto result = jobBoardUploadService.postJobBoard(jobBoardUploadRequest, 1L);

        // then
        assertThat(result.getCompany()).isEqualTo(jobBoardDto.getCompany());
        verify(userRepository).findById(1L);
        verify(jobBoardRepository).save(any(JobBoard.class));
    }

    @Test
    @DisplayName("구인구직 게시글 업로드 - 사용자 정보 없음")
    void testPostJobBoard_UserNotFound() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                jobBoardUploadService.postJobBoard(jobBoardUploadRequest, 1L)
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(USER_INFO_NOT_FOUND);
        verify(userRepository).findById(1L);
        verify(jobBoardRepository, never()).save(any(JobBoard.class));
    }
}
