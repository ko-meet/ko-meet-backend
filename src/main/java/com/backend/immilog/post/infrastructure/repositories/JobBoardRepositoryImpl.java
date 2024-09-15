package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import com.backend.immilog.post.infrastructure.jpa.entities.JobBoardEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.JobBoardJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JobBoardRepositoryImpl implements JobBoardRepository {
    private final JobBoardJpaRepository jobBoardJpaRepository;

    @Override
    public void saveEntity(
            JobBoard jobBoard
    ) {
        jobBoardJpaRepository.save(JobBoardEntity.from(jobBoard));
    }
}