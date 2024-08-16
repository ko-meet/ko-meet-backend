package com.backend.immilog.user.application;

import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;

import java.util.Optional;

import static com.backend.immilog.user.enums.Countries.MALAYSIA;
import static com.backend.immilog.user.enums.Countries.SOUTH_KOREA;
import static com.backend.immilog.user.enums.UserRole.ROLE_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("사용자 정보 서비스 테스트")
class UserInformationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private RedisService redisService;
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userInformationService = new UserInformationService(
                userRepository,
                jwtProvider,
                redisService
        );
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 : 위치 정보 일치")
    void getUserSignInDTO_success() {
        // given
        Long userSeq = 1L;
        User user = User.builder()
                .seq(userSeq)
                .email("test@email.com")
                .nickName("test")
                .imageUrl("image")
                .userStatus(UserStatus.PENDING)
                .userRole(ROLE_USER)
                .interestCountry(SOUTH_KOREA)
                .location(Location.of(SOUTH_KOREA, "Seoul"))
                .reportInfo(null)
                .build();

        Pair<String, String> country = Pair.of("South Korea", "Seoul");
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(jwtProvider.issueAccessToken(any())).thenReturn("accessToken");
        when(jwtProvider.issueRefreshToken()).thenReturn("refreshToken");

        // when
        UserSignInDTO result = userInformationService.getUserSignInDTO(userSeq,
                country);
        // then
        verify(redisService, times(1)).saveKeyAndValue(
                "Refresh: refreshToken", user.getEmail(), 5 * 29 * 24 * 60
        );
        assertThat(result.getUserSeq()).isEqualTo(userSeq);
    }

    @Test
    @DisplayName("사용자 정보 조회 성공 : 위치 정보 불 일치")
    void getUserSignInDTO_success_location_not_match() {
        // given
        Long userSeq = 1L;
        User user = User.builder()
                .seq(userSeq)
                .email("test@email.com")
                .nickName("test")
                .imageUrl("image")
                .userStatus(UserStatus.PENDING)
                .userRole(ROLE_USER)
                .interestCountry(SOUTH_KOREA)
                .location(Location.of(MALAYSIA, "KL"))
                .reportInfo(null)
                .build();

        Pair<String, String> country = Pair.of("South Korea", "Seoul");
        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(jwtProvider.issueAccessToken(any())).thenReturn("accessToken");
        when(jwtProvider.issueRefreshToken()).thenReturn("refreshToken");

        // when
        UserSignInDTO result = userInformationService.getUserSignInDTO(userSeq, country);
        // then
        verify(redisService, times(1)).saveKeyAndValue(
                "Refresh: refreshToken", user.getEmail(), 5 * 29 * 24 * 60
        );
        assertThat(result.getUserSeq()).isEqualTo(userSeq);
    }

}