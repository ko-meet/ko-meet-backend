package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {
        com.backend.immilog.user.domain.model.User userDomain = getUser(email);

        List<GrantedAuthority> authorities =
                new ArrayList<>(userDomain.userRole().getAuthorities());

        return User.builder()
                .username(userDomain.email())
                .password(userDomain.password())
                .authorities(authorities)
                .build();
    }

    private com.backend.immilog.user.domain.model.User getUser(
            String email
    ) {
        return userRepository
                .getByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
