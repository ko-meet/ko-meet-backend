package com.backend.komeet.service.user.service.user;

import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.UserPasswordResetRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.repository.UserRepository;
import com.backend.komeet.service.user.UserInformationService;
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
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userInformationService =
                new UserInformationService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("성공")
    void resetPassword_Success() {
        //given
        User user = User.builder()
                .email("test@test.test")
                .password("test")
                .country(Countries.SOUTH_KOREA)
                .build();

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

}