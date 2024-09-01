package com.backend.immilog.notice.presentation.controller;

import com.backend.immilog.global.enums.UserRole;
import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.notice.application.NoticeInquiryService;
import com.backend.immilog.notice.model.dtos.NoticeDTO;
import com.backend.immilog.notice.model.services.NoticeRegisterService;
import com.backend.immilog.notice.presentation.request.NoticeRegisterRequest;
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
    private final NoticeRegisterService noticeRegisterService;
    private final NoticeInquiryService noticeInquiryService;

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

    @GetMapping
    @ExtractUserId
    @ApiOperation(value = "공지사항 조회", notes = "공지사항을 조회합니다.")
    public ResponseEntity<ApiResponse> getNotices(
            @RequestParam Integer page,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        Page<NoticeDTO> notices = noticeInquiryService.getNotices(userSeq, page);
        return ResponseEntity.status(OK).body(ApiResponse.of(notices));
    }

    @GetMapping("/{noticeSeq}")
    @ApiOperation(value = "공지사항 조회", notes = "공지사항을 조회합니다.")
    public ResponseEntity<ApiResponse> getNoticeDetail(
            @PathVariable Long noticeSeq
    ) {
        NoticeDTO notices = noticeInquiryService.getNoticeDetail(noticeSeq);
        return ResponseEntity.status(OK).body(ApiResponse.of(notices));
    }

}