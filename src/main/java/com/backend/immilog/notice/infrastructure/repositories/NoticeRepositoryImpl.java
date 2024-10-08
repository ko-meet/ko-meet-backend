package com.backend.immilog.notice.infrastructure.repositories;

import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.domain.model.Notice;
import com.backend.immilog.notice.domain.model.enums.NoticeCountry;
import com.backend.immilog.notice.domain.repositories.NoticeRepository;
import com.backend.immilog.notice.infrastructure.jpa.entities.NoticeEntity;
import com.backend.immilog.notice.infrastructure.jpa.entities.QNoticeEntity;
import com.backend.immilog.notice.infrastructure.jpa.repositories.NoticeJpaRepository;
import com.backend.immilog.user.infrastructure.jpa.entity.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final NoticeJpaRepository noticeJpaRepository;

    @Override
    public Page<NoticeResult> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        QNoticeEntity notice = QNoticeEntity.noticeEntity;
        QUserEntity user = QUserEntity.userEntity;
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        predicateBuilder.and(
                notice.targetCountries.any().stringValue()
                        .eq(user.location.country.stringValue())
                        .or(notice.targetCountries.any().stringValue().eq("ALL"))
        );
        Long total = getTotal(predicateBuilder.getValue());
        List<NoticeResult> result =
                jpaQueryFactory
                        .selectFrom(notice)
                        .leftJoin(user)
                        .on(
                                notice.targetCountries.any().stringValue()
                                        .eq(user.location.country.stringValue())
                        )
                        .where(predicateBuilder.getValue())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(notice.createdAt.desc())
                        .fetch()
                        .stream()
                        .map(NoticeEntity::toDomain)
                        .map(NoticeResult::from)
                        .toList();

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

    private Long getTotal(Predicate predicate) {
        QNoticeEntity notice = QNoticeEntity.noticeEntity;
        QUserEntity user = QUserEntity.userEntity;

        return jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .leftJoin(user)
                .on(
                        user.location.country.stringValue()
                                .eq(notice.targetCountries.any().stringValue())
                )
                .where(predicate)
                .fetchOne();
    }
}
