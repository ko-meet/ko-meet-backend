package com.backend.immilog.user.application;

import com.backend.immilog.user.application.services.UserDetailsServiceImpl;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static com.backend.immilog.global.enums.UserRole.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("UserDetailsService 테스트")
class UserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("유저 정보 가져오기")
    void loadUserByUsername() {
        // given
        String email = "test@email.com";
        User user = User.builder()
                .email(email)
                .password("password")
                .userRole(ROLE_USER)
                .build();

        when(userRepository.getByEmail(email)).thenReturn(Optional.of(user));
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // then
        assertThat(userDetails).isNotNull();
    }

}