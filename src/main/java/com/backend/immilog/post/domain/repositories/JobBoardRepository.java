package com.backend.immilog.post.domain.repositories;

import com.backend.immilog.post.domain.model.JobBoard;

public interface JobBoardRepository {
    void saveEntity(JobBoard jobBoard);
}
