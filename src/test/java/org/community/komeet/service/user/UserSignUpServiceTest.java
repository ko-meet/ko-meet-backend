package com.backend.komeet.service.user;

import org.assertj.core.api.Assertions;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.request.UserSignUpRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.exception.ErrorCode.*;
import static org.mockito.Mockito.*;

@DisplayName("사용자 회원가입 서비스 테스트")
public class UserSignUpServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserSignUpService userSignUpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userSignUpService = new UserSignUpService(
                userRepository, passwordEncoder
        );
    }

    UserSignUpRequest request = UserSignUpRequest.builder()
            .email("test@test.com")
            .nickName("test")
            .country(Countries.SOUTH_KOREA)
            .longitude(0.0)
            .latitude(0.0)
            .password("test")
            .build();

    @Test
    @DisplayName("성공")
    public void testSignUp_Success() {

        //given
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(mock(User.class));

        //when
        userSignUpService.signUp(request, CompletableFuture.completedFuture(Pair.of("대한민국", "test"))
        );

        //then
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("실패: 이미 존재하는 사용자")
    public void testSignUp_UserAlreadyExists() {
        //given
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(mock(User.class)));

        //when & then
        Assertions.assertThatThrownBy(() ->
                userSignUpService.signUp(
                                request,
                                CompletableFuture.completedFuture(
                                        Pair.of("대한민국", "test")
                                )))
                        .hasMessage(EXISTING_USER.getMessage())
                        .isInstanceOf(CustomException.class);

    }
}