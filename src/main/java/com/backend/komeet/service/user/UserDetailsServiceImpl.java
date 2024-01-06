package com.backend.komeet.service.user;

import lombok.RequiredArgsConstructor;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security에서 사용자 인증을 위해 사용되는 클래스
 */
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 사용자 인증을 위해 사용자 정보를 조회하는 메서드
     * @param email 사용자 이메일
     * @return UserDetails
     * @throws UsernameNotFoundException 사용자 정보를 찾을 수 없을 때 발생하는 예외
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.backend.komeet.domain.User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFO_NOT_FOUND));

        List<GrantedAuthority> authorities =
                new ArrayList<>(userEntity.getUserRole().getAuthorities());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
    }
}
