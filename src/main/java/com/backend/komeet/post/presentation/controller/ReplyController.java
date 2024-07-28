package com.backend.komeet.post.presentation.controller;

import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.post.presentation.request.CommentUploadRequest;
import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.post.application.reply.ReplyLikeService;
import com.backend.komeet.post.application.reply.ReplyUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * 대댓글 관련 컨트롤러
 */
@Api(tags = "Reply API", description = "대댓글 관련 API")
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
@RestController
public class ReplyController {
    private final ReplyUploadService replyUploadService;
    private final ReplyLikeService replyLikeService;
    private final JwtProvider jwtProvider;

    /**
     * 대댓글 작성
     */
    @PostMapping("/comments/{commentSeq}")
    @ApiOperation(value = "대댓글 작성", notes = "대댓글을 작성합니다.")
    public ResponseEntity<ApiResponse> createReply(
            @PathVariable Long commentSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest
    ) {

        Long userId = jwtProvider.getIdFromToken(token);

        replyUploadService.uploadComment(userId, commentSeq, commentUploadRequest);

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 대댓글 좋아요
     */
    @PatchMapping("/{replySeq}/like")
    @ApiOperation(value = "대댓글 좋아요", notes = "대댓글에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likeReply(
            @PathVariable Long replySeq,
            @RequestHeader(AUTHORIZATION) String token
    ) {

        Long userId = jwtProvider.getIdFromToken(token);

        replyLikeService.likeReply(userId, replySeq);

        return ResponseEntity.status(CREATED).build();
    }
}
