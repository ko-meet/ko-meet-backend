package com.backend.komeet.repository;

import com.backend.komeet.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 대댓글 관련 레포지토리
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
