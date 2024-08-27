package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.model.TokenIssuanceDTO;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.Countries;
import com.backend.immilog.user.enums.UserRole;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.model.interfaces.repositories.UserRepository;
import com.backend.immilog.user.presentation.request.UserSignInRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.user.exception.UserErrorCode.USER_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private CompletableFuture<Pair<String, String>> country;

    private UserSignInService userSignInService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userSignInService = new UserSignInService(userRepository, passwordEncoder, jwtProvider, redisService);
    }

    @Test
    @DisplayName("로그인 성공")
    void sign_in_success() {
        // given
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
                .email("test@email.com")
                .password("test1234")
                .latitude(37.1234)
                .longitude(127.1234)
                .build();

        Location location = Location.builder()
                .country(Countries.SOUTH_KOREA)
                .region("서울")
                .build();

        User user = User.builder()
                .seq(1L)
                .email(userSignInRequest.email())
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.ROLE_USER)
                .password(passwordEncoder.encode(userSignInRequest.password()))
                .location(location)
                .build();

        when(userRepository.findByEmail(userSignInRequest.email()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userSignInRequest.password(), user.getPassword()))
                .thenReturn(true);
        when(jwtProvider.issueAccessToken(any(TokenIssuanceDTO.class)))
                .thenReturn("accessToken");
        when(jwtProvider.issueRefreshToken())
                .thenReturn("refreshToken");
        when(country.orTimeout(5, SECONDS))
                .thenReturn(CompletableFuture.completedFuture(Pair.of("대한민국", "서울")));

        // when
        UserSignInDTO userSignInDTO = userSignInService.signIn(userSignInRequest, country);

        // then
        assertThat(userSignInDTO.userSeq()).isEqualTo(user.getSeq());
        assertThat(userSignInDTO.accessToken()).isEqualTo("accessToken");
        verify(jwtProvider, times(1)).issueAccessToken(any(TokenIssuanceDTO.class));
        verify(jwtProvider, times(1)).issueRefreshToken();
    }

    @Test
    @DisplayName("로그인 실패: 사용자 없음")
    void sign_in_fail_user_not_found() {
        // given
        UserSignInRequest userSignInRequest = UserSignInRequest.builder()
                .email("test@email.com")
                .password("test1234")
                .latitude(37.1234)
                .longitude(127.1234)
                .build();

        Location location = Location.builder()
                .country(Countries.SOUTH_KOREA)
                .region("서울")
                .build();

        User user = User.builder()
                .seq(1L)
                .email(userSignInRequest.email())
                .userStatus(UserStatus.ACTIVE)
                .userRole(UserRole.ROLE_USER)
                .password(passwordEncoder.encode(userSignInRequest.password()))
                .location(location)
                .build();

        // when & then
        assertThatThrownBy(() -> {
            userSignInService.signIn(userSignInRequest, country);
        })
                .isInstanceOf(CustomException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }
}