package com.backend.komeet.post.repositories;

import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.model.entities.QJobBoard;
import com.backend.komeet.user.enums.Countries;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 구인구직 게시판 레포지토리
 */
@RequiredArgsConstructor
@Repository
public class JobBoardQRepositoryImpl implements JobBoardQRepository {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 구인구직 게시판 목록을 조회하는 메서드
     */
    @Override
    public Page<JobBoardDto> getJobBoards(
            String country,
            String sortingMethod,
            String industry,
            String experience,
            Pageable pageable
    ) {

        QJobBoard jobBoard = QJobBoard.jobBoard;
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        // 조건에 맞게 필터링
        if (isNotNullAndSame(industry, Industry.ALL)) {
            predicateBuilder.and(jobBoard.companyMetaData.industry
                    .eq(Industry.valueOf(industry)));
        }
        if (isNotNullAndSame(country, Countries.ALL)) {
            predicateBuilder.and(jobBoard.postMetaData.country
                    .eq(Countries.valueOf(country)));
        }
        if (isNotNullAndSame(experience, Experience.ALL)) {
            predicateBuilder.and(jobBoard.companyMetaData.experience
                    .eq(Experience.valueOf(experience)));
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, jobBoard);

        // 페이지 요청 정보 생성
        List<JobBoardDto> results = jpaQueryFactory
                .select(jobBoard)
                .from(jobBoard)
                .where(predicateBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .stream().map(JobBoardDto::from).collect(Collectors.toList());

        long total = getLength(predicateBuilder);

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 정렬 조건을 반환하는 메서드
     */
    private OrderSpecifier<?> getOrderSpecifier(
            String sortingMethod,
            QJobBoard jobBoard
    ) {
        return switch (SortingMethods.valueOf(sortingMethod)) {
            case VIEW_COUNT -> jobBoard.postMetaData.viewCount.desc();
            case LIKE_COUNT -> jobBoard.postMetaData.likeCount.desc();
            default -> jobBoard.createdAt.desc();
        };
    }

    /**
     * 전체 결과 개수를 조회하는 메서드
     */
    private Long getLength(
            Predicate predicate
    ) {
        QJobBoard jobBoard = QJobBoard.jobBoard;
        return jpaQueryFactory.selectFrom(jobBoard)
                .where(predicate)
                .fetchCount();
    }

    /**
     * 값이 null이 아니고 같은지 확인하는 메서드
     */
    private <T> boolean isNotNullAndSame(
            String value,
            T compare
    ) {
        return value != null && !value.equals(compare.toString());
    }
}
