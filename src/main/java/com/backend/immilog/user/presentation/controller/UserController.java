package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.user.application.EmailService;
import com.backend.immilog.user.application.LocationService;
import com.backend.immilog.user.application.UserSignInService;
import com.backend.immilog.user.application.UserSignUpService;
import com.backend.immilog.user.enums.EmailComponents;
import com.backend.immilog.user.model.dtos.UserSignInDTO;
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

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserSignUpService userSignUpService;
    private final UserSignInService userSignInService;
    private final LocationService locationService;
    private final EmailService emailService;

    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody UserSignUpRequest request
    ) {
        Pair<Long, String> userSeqAndName = userSignUpService.signUp(request);

        emailService.sendHtmlEmail(
                request.getEmail(),
                EmailComponents.EMAIL_SIGN_UP_SUBJECT,
                String.format(
                        EmailComponents.HTML_SIGN_UP_CONTENT,
                        userSeqAndName.getSecond(),
                        String.format(
                                EmailComponents.API_LINK,
                                userSeqAndName.getFirst()
                        )
                )
        );

        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping("/{userSeq}/verification")
    @ApiOperation(value = "사용자 이메일 인증", notes = "사용자 이메일 인증 진행")
    public String verifyEmail(
            @PathVariable Long userSeq,
            Model model
    ) {
        Pair<String, Boolean> result = userSignUpService.verifyEmail(userSeq);
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
        UserSignInDTO userSignInDto = userSignInService.signIn(request, country);
        return ResponseEntity.status(OK).body(new ApiResponse(userSignInDto));
    }
}
