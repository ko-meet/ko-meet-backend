package com.backend.immilog.user.application.services;

import com.backend.immilog.user.exception.UserException;
import com.backend.immilog.user.model.repositories.UserRepository;
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
        com.backend.immilog.user.model.entities.User userEntity = getUser(email);

        List<GrantedAuthority> authorities =
                new ArrayList<>(userEntity.getUserRole().getAuthorities());

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .build();
    }

    private com.backend.immilog.user.model.entities.User getUser(
            String email
    ) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
