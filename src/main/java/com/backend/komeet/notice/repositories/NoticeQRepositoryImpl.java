package com.backend.komeet.notice.repositories;

import com.backend.komeet.notice.model.dtos.NoticeDto;
import com.backend.komeet.notice.model.entities.QNotice;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.model.entities.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import reactor.util.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static com.backend.komeet.post.enums.PostStatus.*;
import static com.backend.komeet.user.enums.Countries.*;

@RequiredArgsConstructor
public class NoticeQRepositoryImpl implements NoticeQRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 사용자에게 보여줄 공지사항 목록을 조회하는 메서드
     *
     * @param user     {@link User}
     * @param pageable {@link Pageable}
     * @return 공지사항 목록
     */
    @Override
    public Page<NoticeDto> getNotices(User user,
                                      Pageable pageable) {
        {
            QNotice notice = QNotice.notice;

            BooleanBuilder predicateBuilder = new BooleanBuilder();

            predicateBuilder.and(notice.targetCountries.contains(user.getCountry())
                    .or(notice.targetCountries.contains(ALL)));
            predicateBuilder.and(notice.status.eq(NORMAL));

            Long total = getTotal(predicateBuilder.getValue());

            List<NoticeDto> result =
                    jpaQueryFactory
                            .selectFrom(notice)
                            .where(predicateBuilder.getValue())
                            .offset(pageable.getOffset())
                            .limit(pageable.getPageSize())
                            .orderBy(notice.createdAt.desc())
                            .fetch()
                            .stream()
                            .map(NoticeDto::from)
                            .collect(Collectors.toList());

            return new PageImpl<>(result, pageable, total);
        }
    }

    /**
     * 공지사항 목록을 조회하는 메서드
     *
     * @param predicate {@link Predicate}
     * @return 공지사항 목록 개 수
     */
    public Long getTotal(@Nullable Predicate predicate) {
        QNotice notice = QNotice.notice;
        return jpaQueryFactory.select(notice.count())
                .from(notice)
                .where(predicate)
                .fetchOne();
    }

}
