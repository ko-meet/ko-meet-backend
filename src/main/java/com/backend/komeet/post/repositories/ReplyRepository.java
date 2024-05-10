package com.backend.komeet.post.repositories;

import com.backend.komeet.post.model.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 대댓글 관련 레포지토리
 */
public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
