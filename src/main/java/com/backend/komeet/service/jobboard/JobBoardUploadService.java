package com.backend.komeet.service.jobboard;

import com.backend.komeet.domain.JobBoard;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.dto.request.JobBoardUploadRequest;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.repository.JobBoardRepository;
import com.backend.komeet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.backend.komeet.exception.ErrorCode.USER_INFO_NOT_FOUND;

/**
 * 구인구직 게시판 업로드 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class JobBoardUploadService {
    private final JobBoardRepository jobBoardRepository;
    private final UserRepository userRepository;

    /**
     * 구인구직 게시글을 업로드하는 메서드
     *
     * @param jobBoardRequest {@link JobBoardUploadRequest}
     * @param userSeq         사용자 식별자
     * @return {@link JobBoardDto}
     */
    public JobBoardDto postJobBoard(JobBoardUploadRequest jobBoardRequest,
                                    Long userSeq) {
        User user = getUser(userSeq);
        return JobBoardDto.from(getJobBoard(jobBoardRequest, user));
    }

    /**
     * 구인구직 게시글 엔티티를 생성하는 메서드
     *
     * @param jobBoardRequest {@link JobBoardUploadRequest}
     * @param user            {@link User}
     * @return {@link JobBoard}
     */
    private JobBoard getJobBoard(JobBoardUploadRequest jobBoardRequest, User user) {
        return jobBoardRepository.save(JobBoard.from(jobBoardRequest, user));
    }

    /**
     * 사용자 엔티티를 조회하는 메서드
     *
     * @param userSeq 사용자 식별자
     * @return {@link User}
     */
    private User getUser(Long userSeq) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
