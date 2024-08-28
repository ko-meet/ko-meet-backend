package com.backend.immilog.global.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class UserIdExtractionAspect {

    private final JwtProvider jwtProvider;
    private final HttpServletRequest request;

    @Before("@annotation(com.backend.immilog.global.security.ExtractUserId)")
    public void extractUserId(JoinPoint joinPoint) {
        // 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Long userSeq = jwtProvider.getIdFromToken(authorizationHeader);
            request.setAttribute("userSeq", userSeq);
        }
    }
}
