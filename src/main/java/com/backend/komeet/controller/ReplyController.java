package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.request.CommentUploadRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.comment.CommentUploadService;
import com.backend.komeet.service.reply.ReplyLikeService;
import com.backend.komeet.service.reply.ReplyUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Reply API", description = "대댓글 관련 API")
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
@RestController
public class ReplyController {
    private final ReplyUploadService replyUploadService;
    private final ReplyLikeService replyLikeService;
    private final JwtProvider jwtProvider;

    @PostMapping("/comments/{commentSeq}")
    @ApiOperation(value = "대댓글 작성", notes = "대댓글을 작성합니다.")
    public ResponseEntity<ApiResponse> createReply(
            @PathVariable Long commentSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        replyUploadService.uploadComment(userId, commentSeq, commentUploadRequest);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }

    @PatchMapping("/{replySeq}/like")
    @ApiOperation(value = "대댓글 좋아요", notes = "대댓글에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likeReply(
            @PathVariable Long replySeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userId = jwtProvider.getIdFromToken(token);

        replyLikeService.likeReply(userId, replySeq);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }
}
