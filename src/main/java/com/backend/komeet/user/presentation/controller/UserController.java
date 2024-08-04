package com.backend.komeet.user.presentation.controller;

import com.backend.komeet.base.application.RedisService;
import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.user.application.*;
import com.backend.komeet.user.enums.EmailComponents;
import com.backend.komeet.user.model.dtos.UserDto;
import com.backend.komeet.user.model.dtos.UserSignInDto;
import com.backend.komeet.user.presentation.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static com.backend.komeet.user.enums.UserStatus.ACTIVE;
import static com.backend.komeet.user.enums.UserStatus.BLOCKED;
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
    private final UserReportService userReportService;

    /**
     * 사용자 회원가입\
     */
    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody UserSignUpRequest userSignUpRequest
    ) {

        Pair<Long, String> userSeqAndNickName =
                userSignUpService.signUp(userSignUpRequest);

        emailService.sendHtmlEmail(
                userSignUpRequest.getEmail(),
                EmailComponents.EMAIL_SIGN_UP_SUBJECT,
                String.format(
                        EmailComponents.HTML_SIGN_UP_CONTENT,
                        userSeqAndNickName.getSecond(),
                        String.format(EmailComponents.API_LINK, userSeqAndNickName.getFirst())
                )
        );

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 이메일 재 인증
     */
    @PostMapping("/authentication-mail")
    @ApiOperation(value = "이메일 재 인증", notes = "이메일 재 인증 진행")
    public ResponseEntity<Void> reAuthenticationMail(
            @Valid @RequestBody UserEmailRequest userEmailRequest
    ) {
        UserDto user =
                userSignUpService.getUserByEmail(userEmailRequest.getEmail());
        emailService.sendHtmlEmail(
                userEmailRequest.getEmail(),
                EmailComponents.EMAIL_SIGN_UP_SUBJECT,
                String.format(
                        EmailComponents.HTML_SIGN_UP_CONTENT,
                        user.getNickName(),
                        String.format(EmailComponents.API_LINK, user.getSeq())
                )
        );
        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 사용자 로그인
     */
    @PostMapping("/sign-in")
    @ApiOperation(value = "사용자 로그인", notes = "사용자 로그인 진행")
    public ResponseEntity<ApiResponse> signIn(
            @Valid @RequestBody UserSignInRequest userSignInRequest
    ) {
        UserDto userDto
                = userSignUpService.getUserByEmail(userSignInRequest.getEmail());
        CompletableFuture<Pair<String, String>> country = geocoderService.getCountry(
                userSignInRequest.getLatitude(),
                userSignInRequest.getLongitude()
        );
        UserSignInDto userSignInDto =
                userSignInService.signIn(userSignInRequest, country);
        return ResponseEntity.status(OK).body(new ApiResponse(userSignInDto));
    }

    /**
     * 사용자 로그아웃
     */
    @PatchMapping("/information")
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자 정보 수정 진행")
    public ResponseEntity<ApiResponse> updateInformation(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody UserInfoUpdateRequest userInfoUpdateRequest
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        CompletableFuture<Pair<String, String>> country =
                geocoderService.getCountry(
                        userInfoUpdateRequest.getLatitude(),
                        userInfoUpdateRequest.getLongitude()
                );
        userInformationService.updateInformation(
                userSeq, country, userInfoUpdateRequest
        );
        return ResponseEntity.status(OK).body(new ApiResponse(OK.value()));
    }

    /**
     * 비밀번호 재설정
     */
    @PatchMapping("/password/reset")
    @ApiOperation(value = "비밀번호 재설정", notes = "비밀번호 재설정 진행")
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid @RequestBody UserPasswordResetRequest passwordResetRequest
    ) {

        String temporaryPassword =
                userInformationService.resetPassword(passwordResetRequest);
//        emailService.sendEmail(
//                passwordResetRequest.getEmail(),
//                PASSWORD_RESET_SUBJECT,
//                String.format(PASSWORD_RESET_CONTENT, temporaryPassword)
//        );
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/password/change")
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경 진행")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody UserPasswordChangeRequest userPasswordChangeRequest
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);

        userInformationService.changePassword(userSeq, userPasswordChangeRequest);

        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 닉네임 중복 체크
     */
    @GetMapping("/nicknames")
    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복 체크 진행")
    public ResponseEntity<ApiResponse> checkNickname(
            @RequestParam String nickname
    ) {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(userInformationService.checkNickname(nickname)));
    }

    /**
     * 사용자 차단
     */
    @PatchMapping("/{userSeq}/block")
    @ApiOperation(value = "사용자 차단", notes = "사용자 차단 진행")
    public ResponseEntity<Void> blockUser(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long userSeq
    ) {
        Long adminSeq = jwtProvider.getIdFromToken(token);
        userInformationService.blockOrUnblockUser(userSeq, adminSeq, BLOCKED);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 사용자 차단 해제
     */
    @PatchMapping("/{userSeq}/unblock")
    @ApiOperation(value = "사용자 차단 해제", notes = "사용자 차단 해제 진행")
    public ResponseEntity<Void> unblockUser(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long userSeq
    ) {
        Long adminSeq = jwtProvider.getIdFromToken(token);
        userInformationService.blockOrUnblockUser(userSeq, adminSeq, ACTIVE);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 사용자 신고
     */
    @PatchMapping("/{userSeq}/report")
    @ApiOperation(value = "사용자 신고", notes = "사용자 신고 진행")
    public ResponseEntity<Void> reportUser(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long userSeq,
            @Valid @RequestBody UserReportRequest userReportRequest
    ) {
        Long reporterSeq = jwtProvider.getIdFromToken(token);
        userReportService.reportValidation(userSeq, reporterSeq);
        userReportService.reportUser(userSeq, reporterSeq, userReportRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
