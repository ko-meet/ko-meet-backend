package com.backend.immilog.global.security;

import com.backend.immilog.global.enums.UserRole;
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

    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;

    @Before("@annotation(com.backend.immilog.global.security.ExtractUserId)")
    public void extractUserId(JoinPoint joinPoint) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            Long userSeq = tokenProvider.getIdFromToken(authorizationHeader);
            request.setAttribute("userSeq", userSeq);
        }
    }
    @Before("@annotation(com.backend.immilog.global.security.ExtractUserId)")
    public void extractUserRole(JoinPoint joinPoint) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            UserRole userRole = tokenProvider.getUserRoleFromToken(authorizationHeader);
            request.setAttribute("userRole", userRole);
        }
    }
}
