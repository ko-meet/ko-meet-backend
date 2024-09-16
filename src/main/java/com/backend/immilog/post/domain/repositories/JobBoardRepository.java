package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobBoardRepository {
    void saveEntity(
            JobBoard jobBoard
    );

    Page<JobBoardResult> getJobBoards(
            Countries country,
            String sortingMethod,
            Industry industry,
            Experience experience,
            Pageable pageable
    );
}
