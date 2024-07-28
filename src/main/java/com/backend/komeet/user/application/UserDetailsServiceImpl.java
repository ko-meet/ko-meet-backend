package com.backend.komeet.user.application;

import lombok.RequiredArgsConstructor;
import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.user.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.backend.komeet.global.exception.ErrorCode.*;

/**
 * Spring Security에서 사용자 인증을 위해 사용되는 클래스
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 사용자 인증을 위해 사용자 정보를 조회하는 메서드
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {
        com.backend.komeet.user.model.entities.User userEntity = getUser(email);

        List<GrantedAuthority> authorities =
                new ArrayList<>(userEntity.getUserRole().getAuthorities());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
    }

    /**
     * 사용자 정보를 조회하는 메서드
     */
    private com.backend.komeet.user.model.entities.User getUser(
            String email
    ) {
        return userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new CustomException(USER_INFO_NOT_FOUND));
    }
}
