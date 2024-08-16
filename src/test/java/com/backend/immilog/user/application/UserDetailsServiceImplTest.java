package com.backend.immilog.user.application;

import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static com.backend.immilog.user.enums.UserRole.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("UserDetailsService 테스트")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

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

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // then
        assertThat(userDetails).isNotNull();
    }

}