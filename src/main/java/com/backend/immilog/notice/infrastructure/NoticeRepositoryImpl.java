package com.backend.immilog.notice.infrastructure;

import com.backend.immilog.notice.application.dtos.NoticeDTO;
import com.backend.immilog.notice.model.entities.QNotice;
import com.backend.immilog.notice.model.repositories.NoticeRepositoryCustom;
import com.backend.immilog.user.model.entities.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<NoticeDTO> getNotices(
            Long userSeq,
            Pageable pageable
    ) {
        QNotice notice = QNotice.notice;
        QUser user = QUser.user;
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        predicateBuilder.and(
                notice.targetCountries.any().stringValue()
                        .eq(user.location.country.stringValue())
                        .or(notice.targetCountries.any().stringValue().eq("ALL"))
        );
        Long total = getTotal(predicateBuilder.getValue());
        List<NoticeDTO> result =
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
                        .map(NoticeDTO::from)
                        .toList();

        return new PageImpl<>(result, pageable, total);
    }

    private Long getTotal(Predicate predicate) {
        QNotice notice = QNotice.notice;
        QUser user = QUser.user;

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
