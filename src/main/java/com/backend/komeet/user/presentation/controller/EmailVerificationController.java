package com.backend.komeet.user.presentation.controller;

import com.backend.komeet.user.application.UserSignUpService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 위치 관련 컨트롤러
 */
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Controller
public class EmailVerificationController {
    private final UserSignUpService userSignUpService;

    /**
     * 사용자 이메일 인증
     *
     * @param userSeq 사용자 시퀀스
     * @return ResponseEntity<Void>
     */
    @GetMapping("/{userSeq}/verification")
    @ApiOperation(value = "사용자 이메일 인증", notes = "사용자 이메일 인증 진행")
    public String verifyEmail(@PathVariable Long userSeq,
                              Model model) {
        Pair<String,Boolean> result = userSignUpService.verifyEmail(userSeq);
        model.addAttribute("message", result.getFirst());
        model.addAttribute("isLoginAvailable", result.getSecond());
        return "verification-result";
    }

}
