package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.domain.model.PostResource;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.model.enums.ResourceType;
import com.backend.immilog.post.domain.repositories.PostResourceRepository;
import com.backend.immilog.post.infrastructure.jdbc.PostResourceJdbcRepository;
import com.backend.immilog.post.infrastructure.jpa.PostResourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PostResourceRepositoryImpl implements PostResourceRepository {
    private final PostResourceJdbcRepository postResourceJdbcRepository;
    private final PostResourceJpaRepository postResourceJpaRepository;

    @Override
    public void deleteAllEntities(
            Long postSeq,
            PostType postType,
            ResourceType resourceType,
            List<String> deleteAttachments
    ) {
        postResourceJdbcRepository.deleteAllEntities(
                postSeq,
                postType,
                resourceType,
                deleteAttachments
        );
    }

    @Override
    public void deleteAllByPostSeq(
            Long seq
    ) {
        postResourceJdbcRepository.deleteAllByPostSeq(seq);
    }

    @Override
    public List<PostResource> findAllByPostSeq(
            Long seq
    ) {
        return postResourceJpaRepository.findAllByPostSeq(seq);
    }
}
