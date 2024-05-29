package com.backend.komeet.notice.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.notice.application.NoticeInquiryService;
import com.backend.komeet.notice.application.NoticeModifyService;
import com.backend.komeet.notice.application.NoticeRegisterService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

/**
 * 공지사항 관련 컨트롤러
 */
@Api(tags = "Notice API", description = "공지사항 관련 API")
@RequestMapping("/api/v1/notice/")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeRegisterService noticeRegisterService;
    private final NoticeInquiryService noticeInquiryService;
    private final NoticeModifyService noticeModifyService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<ApiResponse> registerNotice(@RequestBody NoticeRegisterRequest request,
                                                      @RequestHeader String token) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        noticeRegisterService.registerNotice(userSeq, request);
        return ResponseEntity.status(OK).body(new ApiResponse(true));
    }

}
