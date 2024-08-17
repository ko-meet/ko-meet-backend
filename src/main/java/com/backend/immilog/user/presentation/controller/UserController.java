package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.user.application.*;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
import com.backend.immilog.user.presentation.request.UserInfoUpdateRequest;
import com.backend.immilog.user.presentation.request.UserPasswordChangeRequest;
import com.backend.immilog.user.presentation.request.UserSignInRequest;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static com.backend.immilog.user.enums.EmailComponents.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@Api(tags = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;
    private final UserInformationService userInformationService;

    private final LocationService locationService;
    private final EmailService emailService;

    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody UserSignUpRequest request
    ) {
        final Pair<Long, String> userSeqAndName = userSignUpService.signUp(request);
        final String email = request.getEmail();
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
                        request.getLatitude(),
                        request.getLongitude()
                );
        final UserSignInDTO userSignInDto = userSignInService.signIn(request, country);
        return ResponseEntity.status(OK).body(new ApiResponse(userSignInDto));
    }

    @PatchMapping("/information")
    @ApiOperation(value = "사용자 정보 수정", notes = "사용자 정보 수정 진행")
    public ResponseEntity<ApiResponse> updateInformation(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody UserInfoUpdateRequest userInfoUpdateRequest
    ) {
        final Long userSeq = jwtProvider.getIdFromToken(token);
        CompletableFuture<Pair<String, String>> country =
                locationService.getCountry(
                        userInfoUpdateRequest.getLatitude(),
                        userInfoUpdateRequest.getLongitude()
                );
        userInformationService.updateInformation(
                userSeq, country, userInfoUpdateRequest
        );
        return ResponseEntity.status(OK).body(new ApiResponse(OK.value()));
    }

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

}
