package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.user.application.EmailService;
import com.backend.immilog.user.application.UserSignUpService;
import com.backend.immilog.user.enums.EmailComponents;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;

@DisplayName("사용자 컨트롤러 테스트")
class UserControllerTest {
    @Mock
    private UserSignUpService userSignUpService;
    @Mock
    private EmailService emailService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userSignUpService, emailService);
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
}