package com.backend.immilog.user.application;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.global.application.RedisService;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.embeddables.Location;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserInfoUpdateRequest;
import com.backend.immilog.user.presentation.request.UserPasswordChangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.global.exception.ErrorCode.PASSWORD_NOT_MATCH;
import static com.backend.immilog.user.enums.Countries.*;
import static com.backend.immilog.user.enums.UserRole.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("사용자 정보 서비스 테스트")
class UserInformationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RedisService redisService;
    @Mock
    private ImageService imageService;
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userInformationService = new UserInformationService(
                userRepository,
                jwtProvider,
                passwordEncoder,
                redisService,
                imageService
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

    @Test
    @DisplayName("사용자 정보 업데이트")
    void updateInformation() {
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

        UserInfoUpdateRequest userInfoUpdateRequest =
                UserInfoUpdateRequest.builder()
                        .nickName("newNickName")
                        .profileImage("newImage")
                        .country(JAPAN)
                        .interestCountry(INDONESIA)
                        .latitude(37.123456)
                        .longitude(126.123456)
                        .status(UserStatus.ACTIVE)
                        .build();

        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        CompletableFuture<Pair<String, String>> country =
                CompletableFuture.completedFuture(Pair.of("Japan", "Tokyo"));
        // when
        userInformationService.updateInformation(
                userSeq,
                country,
                userInfoUpdateRequest
        );

        //then
        assertThat(user.getNickName()).isEqualTo("newNickName");
        assertThat(user.getImageUrl()).isEqualTo("newImage");
        assertThat(user.getLocation().getCountry()).isEqualTo(JAPAN);
        assertThat(user.getInterestCountry()).isEqualTo(INDONESIA);
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 - 성공")
    void changePassword_success() {
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
        UserPasswordChangeRequest param = UserPasswordChangeRequest.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "existingPassword",
                user.getPassword()
        )).thenReturn(true);
        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedPassword");

        // when
        userInformationService.changePassword(userSeq, param);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("사용자 비밀번호 변경 - 실패")
    void changePassword_fail() {
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
        UserPasswordChangeRequest param = UserPasswordChangeRequest.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById(userSeq)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(
                "existingPassword",
                user.getPassword()
        )).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userInformationService.changePassword(userSeq, param))
                .isInstanceOf(CustomException.class)
                .hasMessage(PASSWORD_NOT_MATCH.getMessage());
    }

}