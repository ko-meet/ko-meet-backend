package com.backend.komeet.service.user.service.user;

import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.user.application.UserSignUpService;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.user.presentation.request.UserSignUpRequest;
import com.backend.komeet.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.backend.komeet.infrastructure.exception.ErrorCode.EXISTING_USER;
import static com.backend.komeet.user.enums.Countries.SOUTH_KOREA;
import static com.backend.komeet.user.enums.UserRole.ROLE_USER;
import static com.backend.komeet.user.enums.UserStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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

    User user = User.builder()
            .seq(1L)
            .nickName("test")
            .email("test@test.test")
            .password("test")
            .country(SOUTH_KOREA)
            .region("Region")
            .userRole(ROLE_USER)
            .userStatus(PENDING)
            .imageUrl("")
            .build();

    UserSignUpRequest request = UserSignUpRequest.builder()
            .email("test@test.test")
            .nickName("test")
            .country("SOUTH_KOREA")
            .password("test")
            .build();

    @Test
    @DisplayName("성공")
    public void testSignUp_Success() {

        //given
        when(userRepository.findByEmail("test@test.test"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("test");
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        Pair<Long, String> resultPair = userSignUpService.signUp(request);
        //then
        assertThat(resultPair.getFirst()).isEqualTo(1L);
        assertThat(resultPair.getSecond()).isEqualTo(user.getNickName());
    }

    @Test
    @DisplayName("실패: 이미 존재하는 사용자")
    public void testSignUp_UserAlreadyExists() {
        //given
        when(userRepository.findByEmail("test@test.test"))
                .thenReturn(Optional.of(user));

        //when & then
        assertThatThrownBy(() -> userSignUpService.signUp(request))
                .hasMessage(EXISTING_USER.getMessage())
                .isInstanceOf(CustomException.class);

    }
}