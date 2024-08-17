package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.application.*;
import com.backend.immilog.user.enums.EmailComponents;
import com.backend.immilog.user.enums.UserStatus;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.presentation.request.UserInfoUpdateRequest;
import com.backend.immilog.user.presentation.request.UserPasswordChangeRequest;
import com.backend.immilog.user.presentation.request.UserSignInRequest;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.user.enums.Countries.INDONESIA;
import static com.backend.immilog.user.enums.Countries.JAPAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@DisplayName("사용자 컨트롤러 테스트")
class UserControllerTest {
    @Mock
    private UserSignUpService userSignUpService;
    @Mock
    private UserSignInService userSignInService;
    @Mock
    private LocationService locationService;
    @Mock
    private UserInformationService userInformationService;
    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private EmailService emailService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(
                userSignUpService,
                userSignInService,
                userInformationService,
                locationService,
                emailService,
                jwtProvider
        );
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        // given
        UserSignUpRequest param = UserSignUpRequest.builder()
                .nickName("test")
                .password("test1234")
                .email("email@email.com")
                .country("SOUTH_KOREA")
                .interestCountry("SOUTH_KOREA")
                .region("Seoul")
                .profileImage("image")
                .build();

        when(userSignUpService.signUp(param))
                .thenReturn(Pair.of(1L, "test"));

        // when
        ResponseEntity<ApiResponse> response = userController.signUp(param);

        // then
        verify(emailService, times(1)).sendHtmlEmail(
                param.getEmail(),
                EmailComponents.EMAIL_SIGN_UP_SUBJECT,
                String.format(
                        EmailComponents.HTML_SIGN_UP_CONTENT,
                        "test",
                        String.format(
                                EmailComponents.API_LINK,
                                1L
                        )
                )
        );
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    @DisplayName("이메일 인증")
    void verifyEmail() {
        // given
        Long userSeq = 1L;
        String message = "test";
        Boolean isLoginAvailable = true;
        when(userSignUpService.verifyEmail(userSeq))
                .thenReturn(Pair.of(message, isLoginAvailable));
        Model model = mock(Model.class);

        // when
        String result = userController.verifyEmail(userSeq, model);

        // then
        assertThat(result).isEqualTo("verification-result");
    }

    @Test
    @DisplayName("로그인")
    void signIn() {
        // given
        UserSignInRequest param = UserSignInRequest.builder()
                .email("email")
                .password("password")
                .latitude(37.1234)
                .longitude(127.1234)
                .build();


        when(locationService.getCountry(param.getLatitude(), param.getLongitude()))
                .thenReturn(CompletableFuture.completedFuture(Pair.of("대한민국", "서울")));
        when(userSignInService.signIn(param, locationService.getCountry(param.getLatitude(), param.getLongitude())))
                .thenReturn(UserSignInDTO.builder().build());
        // when
        ResponseEntity<ApiResponse> response = userController.signIn(param);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateInformation() {
        // given
        String token = "token";
        UserInfoUpdateRequest param =
                UserInfoUpdateRequest.builder()
                        .nickName("newNickName")
                        .profileImage("newImage")
                        .country(JAPAN)
                        .interestCountry(INDONESIA)
                        .latitude(37.123456)
                        .longitude(126.123456)
                        .status(UserStatus.ACTIVE)
                        .build();

        when(jwtProvider.getIdFromToken(token)).thenReturn(1L);
        when(locationService.getCountry(param.getLatitude(), param.getLongitude()))
                .thenReturn(CompletableFuture.completedFuture(Pair.of("Japan", "Tokyo")));
        // when
        ResponseEntity<ApiResponse> response =
                userController.updateInformation(token, param);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        verify(userInformationService, times(1)).updateInformation(
                1L,
                locationService.getCountry(param.getLatitude(), param.getLongitude()),
                param
        );
    }

    @Test
    @DisplayName("사용자 비밀번호 변경")
    void resetPassword() {
        // given
        String token = "token";
        UserPasswordChangeRequest param = UserPasswordChangeRequest.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();
        when(jwtProvider.getIdFromToken(token)).thenReturn(1L);

        // when
        ResponseEntity<ApiResponse> response =
                userController.changePassword(token,param);

        // then
        verify(userInformationService, times(1))
                .changePassword(1L, param);
        assertThat(response.getStatusCode()).isEqualTo(NO_CONTENT);
    }

    @Test
    @DisplayName("닉네임 중복여부 체크")
    void checkNickname() {
        // given
        String nickname = "test";
        when(userSignUpService.checkNickname(nickname)).thenReturn(true);

        // when
        ResponseEntity<ApiResponse> response =
                userController.checkForNicknameDuplication(nickname);

        // then
        ApiResponse body = Objects.requireNonNull(response.getBody());
        assertThat(body.getData()).isEqualTo(true);
    }
}