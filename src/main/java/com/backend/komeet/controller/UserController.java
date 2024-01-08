package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.UserDto;
import com.backend.komeet.dto.UserSignInDto;
import com.backend.komeet.dto.request.*;
import com.backend.komeet.dto.response.UserSignInResponse;
import com.backend.komeet.service.external.EmailService;
import com.backend.komeet.service.external.GeocoderService;
import com.backend.komeet.service.external.RedisService;
import com.backend.komeet.service.user.UserInformationService;
import com.backend.komeet.service.user.UserSignInService;
import com.backend.komeet.service.user.UserSignUpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.enums.EmailComponents.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

/**
 * 사용자 관련 컨트롤러
 */
@Api(tags = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;
    private final UserInformationService userInformationService;
    private final GeocoderService geocoderService;
    private final EmailService emailService;
    private final RedisService redisService;
    private final JwtProvider jwtProvider;

    /**
     * 사용자 회원가입
     *
     * @param userSignUpRequest 사용자 회원가입 요청 DTO
     * @return ResponseEntity<Void>
     */
    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody UserSignUpRequest userSignUpRequest) {

        CompletableFuture<Pair<String, String>> country = geocoderService.getCountry(
                userSignUpRequest.getLatitude(),
                userSignUpRequest.getLongitude()
        );

        Long userSeq = userSignUpService.signUp(userSignUpRequest, country);

        emailService.sendEmail(
                userSignUpRequest.getEmail(),
                EMAIL_SIGN_UP_SUBJECT,
                String.format(EMAIL_SIGN_UP_CONTENT, userSeq)
        );

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 이메일 재 인증
     *
     * @param userEmailRequest 이메일 재 인증 요청 DTO
     * @return ResponseEntity<Void>
     */
    @PostMapping("/authentication-mail")
    @ApiOperation(value = "이메일 재 인증", notes = "이메일 재 인증 진행")
    public ResponseEntity<Void> reAuthenticationMail(
            @Valid @RequestBody UserEmailRequest userEmailRequest) {

        UserDto user =
                userSignUpService.getUserByEmail(userEmailRequest.getEmail());

        emailService.sendEmail(
                userEmailRequest.getEmail(),
                EMAIL_SIGN_UP_SUBJECT,
                String.format(EMAIL_SIGN_UP_CONTENT, user.getSeq())
        );

        return ResponseEntity.status(OK).build();
    }

    /**
     * 사용자 이메일 인증
     *
     * @param userSeq 사용자 시퀀스
     * @return ResponseEntity<Void>
     */
    @GetMapping("/{userSeq}/verification")
    @ApiOperation(value = "사용자 이메일 인증", notes = "사용자 이메일 인증 진행")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long userSeq) {

        userSignUpService.verifyEmail(userSeq);

        return ResponseEntity.status(OK).build();
    }

    /**
     * 사용자 로그인
     *
     * @param userSignInRequest 사용자 로그인 요청 DTO
     * @return ResponseEntity<UserSignInResponse>
     */
    @PostMapping("/sign-in")
    @ApiOperation(value = "사용자 로그인", notes = "사용자 로그인 진행")
    public ResponseEntity<UserSignInResponse> signIn(
            @Valid @RequestBody UserSignInRequest userSignInRequest) {

        UserDto userDto
                = userSignUpService.getUserByEmail(userSignInRequest.getEmail());

        CompletableFuture<Pair<String, String>> country = geocoderService.getCountry(
                userSignInRequest.getLatitude(),
                userSignInRequest.getLongitude()
        );

        UserSignInDto userSignInDto =
                userSignInService.signIn(userSignInRequest, country);

        redisService.saveRefreshToken(
                userSignInDto.getEmail(),
                userSignInDto.getRefreshToken()
        );

        return ResponseEntity.status(OK).body(UserSignInResponse.from(userSignInDto));
    }

    @PatchMapping("/information")
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자 정보 수정 진행")
    public ResponseEntity<Void> updateInformation(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody UserInfoUpdateRequest userInfoUpdateRequest) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        CompletableFuture<Pair<String, String>> country = geocoderService.getCountry(
                userInfoUpdateRequest.getLatitude(),
                userInfoUpdateRequest.getLongitude()
        );

        userInformationService.updateInformation(
                userSeq, country, userInfoUpdateRequest
        );

        return ResponseEntity.status(OK).build();
    }

    @PatchMapping("/password/reset")
    @ApiOperation(value = "비밀번호 재설정", notes = "비밀번호 재설정 진행")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody UserPasswordResetRequest passwordResetRequest) {

        String temporaryPassword =
                userInformationService.resetPassword(passwordResetRequest);

        emailService.sendEmail(
                passwordResetRequest.getEmail(),
                PASSWORD_RESET_SUBJECT,
                String.format(PASSWORD_RESET_CONTENT, temporaryPassword)
        );

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/password/change")
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경 진행")
    public ResponseEntity<Void> changePassword(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody UserPasswordChangeRequest userPasswordChangeRequest) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        userInformationService.changePassword(userSeq, userPasswordChangeRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }
}
