package com.backend.komeet.repository;

import com.backend.komeet.domain.QJobBoard;
import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.enums.SortingMethods;
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
     *
     * @param country       국가
     * @param sortingMethod 정렬 방식
     * @param industry      업종
     * @param experience    경력
     * @param pageable      페이지 정보
     * @param userSeq       사용자 식별자
     * @return 구인구직 게시판 목록
     */
    @Override
    public Page<JobBoardDto> getJobBoards(String country,
                                          SortingMethods sortingMethod,
                                          Industry industry,
                                          Experience experience,
                                          Pageable pageable,
                                          Long userSeq) {

        QJobBoard jobBoard = QJobBoard.jobBoard;
        BooleanBuilder predicateBuilder = new BooleanBuilder();

        // 조건에 맞게 필터링
        if (industry != null && !industry.equals(Industry.ALL)) {
            predicateBuilder.and(jobBoard.industry.eq(industry));
        }
        if (country != null && !country.equals(Countries.ALL.toString())) {
            predicateBuilder.and(jobBoard.country.eq(Countries.valueOf(country)));
        }
        if (experience != null) {
            predicateBuilder.and(jobBoard.experience.eq(experience));
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
     *
     * @param sortingMethod 정렬 방식
     * @param jobBoard      구인구직 게시판
     * @return 정렬 조건
     */
    private OrderSpecifier<?> getOrderSpecifier(SortingMethods sortingMethod,
                                                QJobBoard jobBoard) {
        switch (sortingMethod) {
            case VIEW_COUNT:
                return jobBoard.viewCount.desc();
            case LIKE_COUNT:
                return jobBoard.likeCount.desc();
            default:
                return jobBoard.createdAt.desc();
        }
    }

    /**
     * 전체 결과 개수를 조회하는 메서드
     *
     * @param predicate 조건
     * @return 전체 결과 개수
     */
    private Long getLength(Predicate predicate) {
        QJobBoard jobBoard = QJobBoard.jobBoard;
        return jpaQueryFactory.selectFrom(jobBoard)
                .where(predicate)
                .fetchCount();
    }
}
