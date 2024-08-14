package com.backend.immilog.user.application;

import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.user.infrastructure.UserRepository;
import com.backend.immilog.user.model.entities.User;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.backend.immilog.global.exception.ErrorCode.EXISTING_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("사용자 회원가입 서비스 테스트")
class UserSignUpServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    private UserSignUpService userSignUpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userSignUpService = new UserSignUpService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signUp_success() {
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

        User userEntity = mock(User.class);
        when(userEntity.getSeq()).thenReturn(1L);
        when(userEntity.getNickName()).thenReturn("test");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("test1234");
        Pair<Long, String> seqAndNickName = userSignUpService.signUp(param);
        // then
        assertThat(seqAndNickName.getSecond()).isEqualTo("test");
    }

    @Test
    @DisplayName("회원가입 - 실패(이미 존재하는 사용자)")
    void signUp_fail_() {
        UserSignUpRequest param = UserSignUpRequest.builder()
                .nickName("test")
                .password("test1234")
                .email("email@email.com")
                .country("SOUTH_KOREA")
                .interestCountry("SOUTH_KOREA")
                .region("Seoul")
                .profileImage("image")
                .build();

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userSignUpService.signUp(param))
                .isInstanceOf(CustomException.class)
                .hasMessage(EXISTING_USER.getMessage());
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복되지 않은 경우")
    void checkNickname_success() {
        // given
        String nickname = "test";
        when(userRepository.findByNickName(nickname)).thenReturn(Optional.empty());
        // when
        Boolean result = userSignUpService.checkNickname(nickname);
        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 확인 - 중복된 경우")
    void checkNickname_fail() {
        // given
        String nickname = "test";
        when(userRepository.findByNickName(nickname)).thenReturn(Optional.of(new User()));
        // when
        Boolean result = userSignUpService.checkNickname(nickname);
        // then
        assertThat(result).isFalse();
    }

}