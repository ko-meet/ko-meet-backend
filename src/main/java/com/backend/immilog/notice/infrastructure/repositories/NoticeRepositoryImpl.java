package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.infrastructure.jdbc.NoticeJdbcRepository;
import com.backend.immilog.notice.infrastructure.jpa.NoticeEntity;
import com.backend.immilog.notice.infrastructure.jpa.NoticeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {
    private final NoticeJdbcRepository noticeJdbcRepository;
    private final NoticeJpaRepository noticeJpaRepository;
    private final JdbcClient jdbcClient;

    @Override
    public Page<NoticeResult> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        List<NoticeResult> result = noticeJdbcRepository.getNotices(
                userSeq,
                pageable.getPageSize(),
                pageable.getOffset()
        );
        Long total = noticeJdbcRepository.getTotal(userSeq);
        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public void saveEntity(Notice notice) {
        noticeJpaRepository.save(NoticeEntity.from(notice));
    }

    @Override
    public Optional<Notice> findBySeq(Long noticeSeq) {
        return noticeJpaRepository.findById(noticeSeq).map(NoticeEntity::toDomain);
    }

    @Override
    public Boolean areUnreadNoticesExist(
            NoticeCountry country,
            Long seq
    ) {
        return noticeJpaRepository.existsByTargetCountriesContainingAndReadUsersNotContaining(
                country,
                seq
        );
    }

    private Long getTotal(
            Long userSeq
    ) {
        String sql = """
                     SELECT COUNT(*) FROM notices n
                         LEFT JOIN users u ON n.target_countries @> ARRAY[u.location_country]::varchar[]
                         WHERE n.target_countries @> ARRAY[u.location_country]::varchar[]
                         OR n.target_countries @> ARRAY['ALL']::varchar[]
                     """;
        return jdbcClient.sql(sql).query((rs, rowNum) -> rs.getLong(1)).single();
    }
}
