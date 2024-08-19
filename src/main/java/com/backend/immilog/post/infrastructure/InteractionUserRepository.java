package com.backend.immilog.post.infrastructure;

import com.backend.immilog.post.model.entities.InteractionUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionUserRepository extends JpaRepository<InteractionUser, Long> {
}
