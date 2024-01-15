package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.request.PostUpdateRequest;
import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.post.PostDeleteService;
import com.backend.komeet.service.post.PostUpdateService;
import com.backend.komeet.service.post.PostUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

/**
 * 게시물 관련 API를 정의한 컨트롤러
 */
@Api(tags = "Post API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostUploadService postUploadService;
    private final PostUpdateService postUpdateService;
    private final PostDeleteService postDeleteService;
    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "게시물 작성", notes = "게시물을 작성합니다.")
    public ResponseEntity<ApiResponse> createPost(
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody PostUploadRequest postUploadRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        postUploadService.uploadPost(userId, postUploadRequest);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }

    @PatchMapping("/{postSeq}")
    @ApiOperation(value = "게시물 수정", notes = "게시물을 수정합니다.")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        postUpdateService.updatePost(userId, postSeq, postUpdateRequest);

        return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(NO_CONTENT.value()));
    }

    @DeleteMapping("/{postSeq}")
    @ApiOperation(value = "게시물 삭제", notes = "게시물을 삭제합니다.")
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userId = jwtProvider.getIdFromToken(token);

        postDeleteService.deletePost(userId, postSeq);

        return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(NO_CONTENT.value()));
    }
}