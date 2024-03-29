package com.backend.komeet.service.user.service.user;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.TokenIssuanceDto;
import com.backend.komeet.dto.UserSignInDto;
import com.backend.komeet.dto.request.UserSignInRequest;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.exception.CustomException;
import com.backend.komeet.exception.ErrorCode;
import com.backend.komeet.repository.UserRepository;
import com.backend.komeet.service.external.RedisService;
import com.backend.komeet.service.user.UserSignInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.backend.komeet.enums.UserRole.ROLE_USER;
import static com.backend.komeet.enums.UserStatus.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("사용자 로그인 서비스 테스트")
class UserSignInServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RedisService redisService;


    private UserSignInService userSignInService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userSignInService = new UserSignInService(
                userRepository, passwordEncoder, jwtProvider,redisService
        );
    }

    User user = User.builder()
            .email("test@test.com")
            .password("test")
            .userRole(ROLE_USER)
            .userStatus(ACTIVE)
            .seq(1L)
            .region("test")
            .country(Countries.ECT)
            .build();

    @Test
    @DisplayName("성공")
    void testSignIn_Success() {
        // Given
        UserSignInRequest signInRequest =
                new UserSignInRequest("test@test.com", "test",0.0,0.0);
        CompletableFuture<Pair<String, String>> country =
                CompletableFuture.completedFuture(Pair.of("CountryName", "CityName"));

        when(userRepository.findByEmail(signInRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("test", "test"))
                .thenReturn(true);
        when(jwtProvider.issueAccessToken(any(TokenIssuanceDto.class)))
                .thenReturn("access_token");
        when(jwtProvider.issueRefreshToken())
                .thenReturn("refresh_token");

        // When
        UserSignInDto signInDto = userSignInService.signIn(signInRequest, country);

        // Then
        assertThat(signInDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("실패: 사용자 정보를 찾을 수 없음")
    void testSignIn_InvalidPassword() {
        // Given
        UserSignInRequest signInRequest =
                new UserSignInRequest("test@example.com", "password",0.0,0.0);
        User user = new User();
        CompletableFuture<Pair<String, String>> country =
                CompletableFuture.completedFuture(Pair.of("CountryName", "CityName"));

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> userSignInService.signIn(signInRequest, country))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.PASSWORD_NOT_MATCH.getMessage());
    }
}
