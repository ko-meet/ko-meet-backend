package com.backend.komeet.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * QueryDSL 설정 클래스
 */
@Configuration
public class QueryDSLConfig {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Bean으로 등록된 JPAQueryFactory를 반환하는 메서드
     * JPAQueryFactory는 QueryDSL을 사용하여 JPA 쿼리를 생성하는 데 사용
     *
     * @return JPAQueryFactory 인스턴스
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
