package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.command.JobBoardUploadCommand;
import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.application.services.CompanyInquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobBoardUploadService {
    private final JobBoardRepository jobBoardRepository;
    private final CompanyInquiryService companyInquiryService;

    @Transactional
    public void uploadJobBoard(
            Long userSeq,
            JobBoardUploadCommand command
    ) {
        CompanyResult company = companyInquiryService.getCompany(userSeq);
        JobBoard jobBoard = JobBoard.of(userSeq, company.toDomain(), command);
        jobBoardRepository.saveEntity(jobBoard);
    }
}
