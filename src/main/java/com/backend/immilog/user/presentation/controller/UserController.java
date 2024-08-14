package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.user.application.EmailService;
import com.backend.immilog.user.application.UserSignUpService;
import com.backend.immilog.user.enums.EmailComponents;
import com.backend.immilog.user.presentation.request.UserSignUpRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserSignUpService userSignUpService;
    private final EmailService emailService;

    @PostMapping
    @ApiOperation(value = "사용자 회원가입", notes = "사용자 회원가입 진행")
    public ResponseEntity<ApiResponse> signUp(
            @Valid @RequestBody UserSignUpRequest userSignUpRequest
    ) {
        Pair<Long, String> userSeqAndName = userSignUpService.signUp(userSignUpRequest);

        emailService.sendHtmlEmail(
                userSignUpRequest.getEmail(),
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

}
