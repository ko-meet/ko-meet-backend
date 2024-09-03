package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.post.application.services.CommentUploadService;
import com.backend.immilog.post.presentation.request.CommentUploadRequest;
import com.backend.immilog.post.presentation.response.PostApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Comment API", description = "댓글 관련 API")
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentUploadService commentUploadService;

    @PostMapping("/{referenceType}/{postSeq}")
    @ExtractUserId
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성합니다.")
    public ResponseEntity<PostApiResponse> createComment(
            @PathVariable String referenceType,
            @PathVariable Long postSeq,
            HttpServletRequest request,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        commentUploadService.uploadComment(
                userSeq,
                postSeq,
                referenceType,
                commentUploadRequest.content()
        );
        return ResponseEntity.status(CREATED).build();
    }
}
