package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.application.services.JobBoardInquiryService;
import com.backend.immilog.post.application.services.JobBoardUploadService;
import com.backend.immilog.post.presentation.request.JobBoardUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "JobBoard API", description = "구인구직 업로드 관련 API")
@RequestMapping("/api/v1/job-boards")
@RequiredArgsConstructor
@RestController
public class JobBoardController {
    private final JobBoardUploadService jobBoardUploadService;
    private final JobBoardInquiryService jobBoardInquiryService;

    @PostMapping
    @ExtractUserId
    @ApiOperation(value = "구인구직 게시글 업로드", notes = "구인구직 게시글을 업로드합니다.")
    public ResponseEntity<PostApiResponse> uploadJobBoard(
            HttpServletRequest request,
            @RequestBody JobBoardUploadRequest jobBoardRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        jobBoardUploadService.uploadJobBoard(userSeq, jobBoardRequest.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    @ApiOperation(value = "구인구직 게시글 조회", notes = "구인구직 게시글을 조회합니다.")
    public ResponseEntity<PostApiResponse> searchJobBoard(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortingMethod,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) Integer page
    ) {
        Page<JobBoardResult> jobBoards = jobBoardInquiryService.getJobBoards(
                country,
                sortingMethod,
                industry,
                experience,
                page
        );
        return ResponseEntity.status(OK).body(PostApiResponse.of(jobBoards));
    }

}
