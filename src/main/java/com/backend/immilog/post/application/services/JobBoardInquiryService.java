package com.backend.immilog.post.application.services;

import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.Industry;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobBoardInquiryService {
    private final JobBoardRepository jobBoardRepository;

    @Transactional(readOnly = true)
    public Page<JobBoardResult> getJobBoards(
            String country,
            String sortingMethod,
            String industry,
            String experience,
            Integer page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        Industry industryEnum = Industry.valueOf(industry);
        Countries countryEnum = Countries.valueOf(country);
        Experience experienceEnum = Experience.valueOf(experience);

        return jobBoardRepository.getJobBoards(
                countryEnum,
                sortingMethod,
                industryEnum,
                experienceEnum,
                pageable
        );
    }

}
