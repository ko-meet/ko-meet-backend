package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.JobBoardDto;
import com.backend.komeet.dto.request.JobBoardUploadRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.enums.Experience;
import com.backend.komeet.enums.Industry;
import com.backend.komeet.service.jobboard.JobBoardDetailService;
import com.backend.komeet.service.jobboard.JobBoardSearchService;
import com.backend.komeet.service.jobboard.JobBoardUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

/**
 * 구인구직 게시판 관련 API를 정의한 컨트롤러
 */
@Api(tags = "JobBoard API", description = "구인구직 업로드 관련 API")
@RequestMapping("/api/v1/job-boards")
@RequiredArgsConstructor
@RestController
public class JobBoardController {
    private final JobBoardUploadService jobBoardUploadService;
    private final JobBoardSearchService jobBoardSearchService;
    private final JobBoardDetailService jobBoardDetailService;
    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "구인구직 게시글 업로드", notes = "구인구직 게시글을 업로드합니다.")
    public ResponseEntity<ApiResponse> uploadJobBoard(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestBody JobBoardUploadRequest jobBoardRequest
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        JobBoardDto jobBoardDto =
                jobBoardUploadService.postJobBoard(jobBoardRequest, userSeq);
        return ResponseEntity.status(OK).body(new ApiResponse(jobBoardDto));
    }
}
