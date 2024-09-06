package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.infrastructure.jpa.entities.InteractionUserEntity;
import com.backend.immilog.post.infrastructure.jpa.repository.InteractionUserJpaRepository;
import com.backend.immilog.post.domain.model.InteractionUser;
import com.backend.immilog.post.domain.repositories.InteractionUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class InteractionUserRepositoryImpl implements InteractionUserRepository {
    private final InteractionUserJpaRepository interactionUserJpaRepository;

    @Override
    public List<InteractionUser> getByPostSeq(
            Long postSeq
    ) {
        return interactionUserJpaRepository.findByPostSeq(postSeq)
                .stream()
                .map(InteractionUserEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteEntity(
            InteractionUser interactionUser
    ) {
        interactionUserJpaRepository.delete(InteractionUserEntity.from(interactionUser));
    }

    @Override
    public void saveEntity(
            InteractionUser likeUser
    ) {
        interactionUserJpaRepository.save(InteractionUserEntity.from(likeUser));
    }
}
