package com.backend.komeet.post.application.jobboard;

import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.USER_INFO_NOT_FOUND;

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
    @Transactional
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
