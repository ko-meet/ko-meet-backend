package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.request.CommentUploadRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.comment.CommentUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Comment API", description = "댓글 관련 API")
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentUploadService commentUploadService;
    private final JwtProvider jwtProvider;

    @PostMapping("/posts/{postSeq}")
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        commentUploadService.uploadComment(userId, postSeq, commentUploadRequest);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }
}
