package com.backend.komeet.service.user.service.user;

import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserPasswordChangeRequest;
import com.backend.komeet.user.presentation.request.UserPasswordResetRequest;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.user.repositories.UserRepository;
import com.backend.komeet.base.application.RedisService;
import com.backend.komeet.user.application.UserInformationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@DisplayName("사용자 비밀번호 초기화 테스트")
class UserInformationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RedisService redisService;
    @Mock
    private JwtProvider jwtProvider;
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userInformationService = new UserInformationService(
                userRepository, passwordEncoder, redisService, jwtProvider
        );
    }

    User user = User.builder()
            .email("test@test.test")
            .password("test")
            .country(Countries.SOUTH_KOREA)
            .build();

    @Test
    @DisplayName("성공")
    void resetPassword_Success() {
        //given
        UserPasswordResetRequest userPasswordResetRequest =
                UserPasswordResetRequest.builder()
                        .email("test@test.test")
                        .country(Countries.SOUTH_KOREA)
                        .build();

        when(userRepository.findByEmail(userPasswordResetRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(Mockito.anyString()))
                .thenReturn("encodedPw");
        //when
        String tempPassword =
                userInformationService.resetPassword(userPasswordResetRequest);
        //then
        Assertions.assertThat(tempPassword).isNotEqualTo("test");
    }

    @Test
    @DisplayName("성공 - 비밀번호 변경")
    void changePassword_Success() {
        //given
        UserPasswordChangeRequest userPasswordChangeRequest =
                UserPasswordChangeRequest.builder()
                        .existingPassword("test")
                        .newPassword("test2")
                        .build();

        when(userRepository.findById(user.getSeq()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test", user.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.encode("test2"))
                .thenReturn("encodedPw");
        //when
        userInformationService.changePassword(user.getSeq(), userPasswordChangeRequest);

        //then
        Assertions.assertThat(user.getPassword()).isEqualTo("encodedPw");

    }
}