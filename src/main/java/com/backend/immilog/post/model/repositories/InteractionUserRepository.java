package com.backend.immilog.post.model.repositories;

import com.backend.immilog.post.model.entities.InteractionUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionUserRepository extends JpaRepository<InteractionUser, Long> {
    List<InteractionUser> findByPostSeq(Long postSeq);
}
