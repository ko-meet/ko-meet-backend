package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.user.application.services.*;
import com.backend.immilog.user.application.services.UserReportService;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.model.enums.UserStatus;
import com.backend.immilog.user.presentation.request.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.user.enums.EmailComponents.*;
import static org.springframework.http.HttpStatus.*;

@Api(tags = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;
    private final UserInformationService userInformationService;
    private final UserReportService userReportService;

    private final LocationService locationService;
    private final EmailService emailService;

    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody UserSignUpRequest request
    ) {
        final Pair<Long, String> userSeqAndName = userSignUpService.signUp(request.toCommand());
        final String email = request.email();
        final String userName = userSeqAndName.getSecond();
        final Long userSeq = userSeqAndName.getFirst();
        final String url = String.format(API_LINK, userSeq);
        final String mailForm = String.format(HTML_SIGN_UP_CONTENT, userName, url);
        emailService.sendHtmlEmail(email, EMAIL_SIGN_UP_SUBJECT, mailForm);
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/{userSeq}/verification")
    @ApiOperation(value = "사용자 이메일 인증", notes = "사용자 이메일 인증 진행")
    public String verifyEmail(
            @PathVariable Long userSeq,
            Model model
    ) {
        final Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
        model.addAttribute("message", result.getFirst());
        model.addAttribute("isLoginAvailable", result.getSecond());
        return "verification-result";
    }

    @PostMapping("/sign-in")
    @ApiOperation(value = "사용자 로그인", notes = "사용자 로그인 진행")
    public ResponseEntity<ApiResponse> signIn(
            @Valid @RequestBody UserSignInRequest request
    ) {
        CompletableFuture<Pair<String, String>> country =
                locationService.getCountry(
                        request.latitude(),
                        request.longitude()
                );
        final UserSignInDTO userSignInDTO = userSignInService.signIn(request.toCommand(), country);
        return ResponseEntity.status(OK).body(ApiResponse.of(userSignInDTO));
    }

    @PatchMapping("/information")
    @ExtractUserId
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자 정보 수정 진행")
    public ResponseEntity<ApiResponse> updateInformation(
            HttpServletRequest request,
            @RequestBody UserInfoUpdateRequest userInfoUpdateRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        CompletableFuture<Pair<String, String>> country =
                locationService.getCountry(
                        userInfoUpdateRequest.latitude(),
                        userInfoUpdateRequest.longitude()
                );
        userInformationService.updateInformation(
                userSeq, country, userInfoUpdateRequest.toCommand()
        );
        return ResponseEntity.status(OK).body(ApiResponse.of(OK.value()));
    }

    @PatchMapping("/password/change")
    @ExtractUserId
    @ApiOperation(value = "비밀번호 변경", notes = "비밀번호 변경 진행")
    public ResponseEntity<ApiResponse> changePassword(
            HttpServletRequest request,
            @RequestBody UserPasswordChangeRequest userPasswordChangeRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        userInformationService.changePassword(
                userSeq,
                userPasswordChangeRequest.toCommand()
        );

        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping("/nicknames")
    @ExtractUserId
    @ApiOperation(value = "닉네임 중복 체크", notes = "닉네임 중복 체크 진행")
    public ResponseEntity<ApiResponse> checkNickname(
            @RequestParam String nickname
    ) {
        Boolean isNickNameAvailable = userSignUpService.checkNickname(nickname);
        return ResponseEntity.status(OK).body(ApiResponse.of(isNickNameAvailable));
    }

    @PatchMapping("/{userSeq}/{status}")
    @ApiOperation(value = "사용자 차단/해제", notes = "사용자 차단/해제 진행")
    public ResponseEntity<Void> blockUser(
            HttpServletRequest request,
            @PathVariable Long userSeq,
            @PathVariable String status
    ) {
        Long adminSeq = (Long) request.getAttribute("userSeq");
        UserStatus userStatus = UserStatus.valueOf(status);
        userInformationService.blockOrUnblockUser(userSeq, adminSeq, userStatus);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{userSeq}/report")
    @ExtractUserId
    @ApiOperation(value = "사용자 신고", notes = "사용자 신고 진행")
    public ResponseEntity<Void> reportUser(
            HttpServletRequest request,
            @PathVariable Long userSeq,
            @Valid @RequestBody UserReportRequest userReportRequest
    ) {
        Long reporterSeq = (Long) request.getAttribute("userSeq");
        userReportService.reportUser(
                userSeq,
                reporterSeq,
                userReportRequest.toCommand()
        );
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
