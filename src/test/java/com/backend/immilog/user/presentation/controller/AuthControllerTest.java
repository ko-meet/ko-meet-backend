package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.application.LocationService;
import com.backend.immilog.user.application.UserInformationService;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("인증 컨트롤러 테스트")
class AuthControllerTest {
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private LocationService locationService;
    @Mock
    private UserInformationService userInformationService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(
                jwtProvider,
                locationService,
                userInformationService
        );
    }

    @Test
    @DisplayName("사용자 정보 조회")
    void getUser() {
        // given
        Long userSeq = 1L;
        String token = "Bearer token";
        Double latitude = 37.123456;
        Double longitude = 126.123456;
        UserSignInDTO userSignInDTO = UserSignInDTO.builder()
                .userSeq(userSeq)
                .email("test@email.com")
                .nickname("test")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .country("South Korea")
                .interestCountry("South Korea")
                .region("Seoul")
                .userProfileUrl("image")
                .isLocationMatch(true)
                .build();

        Pair<String, String> location = Pair.of("KR", "South Korea");
        CompletableFuture<Pair<String, String>> value =
                CompletableFuture.completedFuture(location);
        when(locationService.getCountry(latitude, longitude)).thenReturn(value);
        when(locationService.joinCompletableFutureLocation(value)).thenReturn(location);
        when(jwtProvider.getIdFromToken(token)).thenReturn(userSeq);
        when(userInformationService.getUserSignInDTO(userSeq, location)).thenReturn(userSignInDTO);

        // when
        ResponseEntity<ApiResponse> response =
                authController.getUser(token, latitude, longitude);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK);
        Object data = Objects.requireNonNull(response.getBody()).data();
        assertThat(((UserSignInDTO) data).email()).isEqualTo(userSignInDTO.email());
    }
}