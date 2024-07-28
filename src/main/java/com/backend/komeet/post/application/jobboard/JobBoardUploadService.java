package com.backend.komeet.post.application.jobboard;

import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.company.repositories.CompanyRepository;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.post.presentation.request.JobBoardUploadRequest;
import com.backend.komeet.post.repositories.JobBoardRepository;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.COMPANY_NOT_FOUND;
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
    private final CompanyRepository companyRepository;

    /**
     * 구인구직 게시글을 업로드하는 메서드
     */
    @Transactional
    public JobBoardDto postJobBoard(
            JobBoardUploadRequest jobBoardRequest,
            Long userSeq
    ) {
        User user = getUser(userSeq);
        return JobBoardDto.from(getJobBoard(jobBoardRequest, user));
    }

    /**
     * 구인구직 게시글 엔티티를 생성하는 메서드
     */
    private JobBoard getJobBoard(
            JobBoardUploadRequest jobBoardRequest,
            User user
    ) {
        Company company = getCompany(jobBoardRequest.getCompanySeq());
        return jobBoardRepository.save(JobBoard.from(jobBoardRequest, company, user));
    }

    /**
     * 사용자 엔티티를 조회하는 메서드
     */
    private User getUser(
            Long userSeq
    ) {
        return userRepository
                .findById(userSeq)
                .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }

    private Company getCompany(
            Long companySeq
    ) {
        return companyRepository
                .findById(companySeq)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));
    }
}
