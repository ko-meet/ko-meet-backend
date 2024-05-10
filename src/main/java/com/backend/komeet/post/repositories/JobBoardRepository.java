package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.JobBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 구인구직 게시판 레포지토리
 */
@Repository
public interface JobBoardRepository extends JpaRepository<JobBoard, Long>, JobBoardQRepository {
}
