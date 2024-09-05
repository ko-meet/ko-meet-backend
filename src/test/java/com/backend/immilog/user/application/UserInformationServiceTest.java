package com.backend.immilog.user.application;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.user.application.command.UserPasswordChangeCommand;
import com.backend.immilog.user.application.services.UserInformationService;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.model.enums.UserStatus;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.domain.model.vo.Location;
import com.backend.immilog.user.exception.UserException;
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

import static com.backend.immilog.global.enums.UserRole.ROLE_ADMIN;
import static com.backend.immilog.global.enums.UserRole.ROLE_USER;
import static com.backend.immilog.user.domain.model.enums.UserCountry.*;
import static com.backend.immilog.user.exception.UserErrorCode.NOT_AN_ADMIN_USER;
import static com.backend.immilog.user.exception.UserErrorCode.PASSWORD_NOT_MATCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("사용자 정보 서비스 테스트")
class UserInformationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageService imageService;
    private UserInformationService userInformationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userInformationService = new UserInformationService(
                userRepository,
                passwordEncoder,
                imageService
        );
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

        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        CompletableFuture<Pair<String, String>> country =
                CompletableFuture.completedFuture(Pair.of("Japan", "Tokyo"));
        // when
        userInformationService.updateInformation(
                userSeq,
                country,
                param.toCommand()
        );
        //then
        verify(userRepository).saveEntity(any());
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
        UserPasswordChangeCommand param = UserPasswordChangeCommand.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("existingPassword", user.password())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // when
        userInformationService.changePassword(userSeq, param);

        // then
        verify(userRepository).saveEntity(any());
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

        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("existingPassword", user.password())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userInformationService.changePassword(userSeq, param.toCommand()))
                .isInstanceOf(UserException.class)
                .hasMessage(PASSWORD_NOT_MATCH.getMessage());
    }

    @Test
    @DisplayName("사용자 차단/해제 - 성공")
    void blockOrUnblockUser() {
        // given
        Long userSeq = 1L;
        Long adminSeq = 2L;
        UserStatus userStatus = UserStatus.BLOCKED;
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
                .userStatus(UserStatus.ACTIVE)
                .build();
        User admin = User.builder()
                .seq(userSeq)
                .email("test@email.com")
                .nickName("test")
                .imageUrl("image")
                .userStatus(UserStatus.PENDING)
                .userRole(ROLE_ADMIN)
                .interestCountry(SOUTH_KOREA)
                .location(Location.of(MALAYSIA, "KL"))
                .reportInfo(null)
                .build();
        when(userRepository.getById(userSeq)).thenReturn(Optional.of(user));
        when(userRepository.getById(adminSeq)).thenReturn(Optional.of(admin));

        // when
        userInformationService.blockOrUnblockUser(userSeq, adminSeq, userStatus);

        // then
        verify(userRepository).saveEntity(any());
    }

    @Test
    @DisplayName("사용자 차단/해제 - 실패:관리자 아님")
    void blockOrUnblockUser_fail() {
        // given
        Long userSeq = 1L;
        Long adminSeq = 2L;
        UserStatus userStatus = UserStatus.BLOCKED;
        User admin = User.builder()
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
        when(userRepository.getById(adminSeq)).thenReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> userInformationService.blockOrUnblockUser(
                userSeq,
                adminSeq,
                userStatus
        ))
                .isInstanceOf(UserException.class)
                .hasMessage(NOT_AN_ADMIN_USER.getMessage());
    }
}