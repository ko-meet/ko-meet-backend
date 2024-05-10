package com.backend.komeet.post.presentation.controller;

import com.backend.komeet.infrastructure.security.JwtProvider;
import com.backend.komeet.post.presentation.request.CommentUploadRequest;
import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.post.application.CommentLikeService;
import com.backend.komeet.post.application.CommentUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * 댓글 관련 컨트롤러
 */
@Api(tags = "Comment API", description = "댓글 관련 API")
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentUploadService commentUploadService;
    private final CommentLikeService commentLikeService;
    private final JwtProvider jwtProvider;

    /**
     * 댓글 작성
     *
     * @param postSeq              게시물 번호
     * @param token                토큰
     * @param commentUploadRequest {@link CommentUploadRequest} 댓글 작성 요청
     * @return {@link ResponseEntity<ApiResponse>} 댓글 작성 결과
     */
    @PostMapping("/posts/{postSeq}")
    @ApiOperation(value = "댓글 작성", notes = "댓글을 작성합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody CommentUploadRequest commentUploadRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        commentUploadService.uploadComment(userId, postSeq, commentUploadRequest);

        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 댓글 좋아요
     *
     * @param commentSeq 댓글 번호
     * @param token      토큰
     * @return {@link ResponseEntity<ApiResponse>} 댓글 좋아요 결과
     */
    @PatchMapping("/{commentSeq}/like")
    @ApiOperation(value = "댓글 좋아요", notes = "댓글에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likeComment(
            @PathVariable Long commentSeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userId = jwtProvider.getIdFromToken(token);

        commentLikeService.likeComment(userId, commentSeq);

        return ResponseEntity.status(CREATED).build();
    }
}
