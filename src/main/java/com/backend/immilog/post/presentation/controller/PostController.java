package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.JwtProvider;
import com.backend.immilog.post.application.PostDeleteService;
import com.backend.immilog.post.application.PostInquiryService;
import com.backend.immilog.post.application.PostUpdateService;
import com.backend.immilog.post.application.PostUploadService;
import com.backend.immilog.post.enums.Categories;
import com.backend.immilog.post.enums.SortingMethods;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import com.backend.immilog.user.enums.Countries;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@Api(tags = "Post API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostUploadService postUploadService;
    private final PostUpdateService postUpdateService;
    private final PostDeleteService postDeleteService;
    private final PostInquiryService postInquiryService;
    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "게시물 작성", notes = "게시물을 작성합니다.")
    public ResponseEntity<ApiResponse> createPost(
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody PostUploadRequest postUploadRequest
    ) {
        Long userId = jwtProvider.getIdFromToken(token);
        postUploadService.uploadPost(userId, postUploadRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{postSeq}")
    @ApiOperation(value = "게시물 수정", notes = "게시물을 수정합니다.")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        Long userId = jwtProvider.getIdFromToken(token);
        postUpdateService.updatePost(userId, postSeq, postUpdateRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/delete")
    @ApiOperation(value = "게시물 삭제", notes = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userId = jwtProvider.getIdFromToken(token);
        postDeleteService.deletePost(userId, postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/view")
    @ApiOperation(value = "게시물 조회수 증가", notes = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<ApiResponse> increaseViewCount(
            @PathVariable Long postSeq
    ) {
        postUpdateService.increaseViewCount(postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/like")
    @ApiOperation(value = "게시물 좋아요", notes = "게시물에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userId = jwtProvider.getIdFromToken(token);
        postUpdateService.likePost(userId, postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @GetMapping
    @ApiOperation(value = "게시물 목록 조회", notes = "게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getPosts(
            @RequestParam(required = false) Countries country,
            @RequestParam(required = false) SortingMethods sortingMethod,
            @RequestParam(required = false) String isPublic,
            @RequestParam(required = false) Categories category,
            @RequestParam(required = false) Integer page
    ) {
        Page<PostDTO> posts = postInquiryService.getPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                page == null ? 0 : page
        );
        return ResponseEntity.status(OK).body(ApiResponse.of(posts));
    }

}
