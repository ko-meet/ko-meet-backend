package com.backend.immilog.notice.presentation.controller;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.notice.application.result.NoticeResult;
import com.backend.immilog.notice.application.services.NoticeCreateService;
import com.backend.immilog.notice.application.services.NoticeInquiryService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
import com.backend.immilog.notice.presentation.response.NoticeApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Notice API", description = "공지사항 관련 API")
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@RestController
public class NoticeController {
    private final NoticeCreateService noticeRegisterService;
    private final NoticeInquiryService noticeInquiryService;

    @PostMapping
    @ExtractUserId
    @ApiOperation(value = "공지사항 등록", notes = "공지사항을 등록합니다.")
    public ResponseEntity<NoticeApiResponse> registerNotice(
            @RequestBody NoticeRegisterRequest noticeRegisterRequest,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        String userRole = ((UserRole) request.getAttribute("userRole")).name();
        noticeRegisterService.registerNotice(userSeq, userRole, noticeRegisterRequest.toCommand());
        return ResponseEntity
                .status(CREATED)
                .body(NoticeApiResponse.of(true));
    }

    @GetMapping
    @ExtractUserId
    @ApiOperation(value = "공지사항 조회", notes = "공지사항을 조회합니다.")
    public ResponseEntity<NoticeApiResponse> getNotices(
            @RequestParam Integer page,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        Page<NoticeResult> notices = noticeInquiryService.getNotices(userSeq, page);
        return ResponseEntity.status(OK).body(NoticeApiResponse.of(notices));
    }

    @GetMapping("/{noticeSeq}")
    @ApiOperation(value = "공지사항 조회", notes = "공지사항을 조회합니다.")
    public ResponseEntity<NoticeApiResponse> getNoticeDetail(
            @PathVariable Long noticeSeq
    ) {
        NoticeResult notices = noticeInquiryService.getNoticeDetail(noticeSeq);
        return ResponseEntity.status(OK).body(NoticeApiResponse.of(notices));
    }

}