package com.backend.immilog.notice.presentation.controller;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.notice.model.services.NoticeRegisterService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Notice API", description = "공지사항 관련 API")
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeRegisterService noticeRegisterService;

    @PostMapping
    @ExtractUserId
    @ApiOperation(value = "공지사항 등록", notes = "공지사항을 등록합니다.")
    public ResponseEntity<ApiResponse> registerNotice(
            @RequestBody NoticeRegisterRequest noticeRegisterRequest,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        UserRole userRole = (UserRole) request.getAttribute("userRole");
        noticeRegisterService.registerNotice(userSeq, userRole, noticeRegisterRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ApiResponse.of(true));
    }
}