package com.backend.immilog.post.presentation.controller;

import com.backend.immilog.global.presentation.response.ApiResponse;
import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.post.application.services.PostDeleteService;
import com.backend.immilog.post.application.services.PostInquiryService;
import com.backend.immilog.post.application.services.PostUpdateService;
import com.backend.immilog.post.application.services.PostUploadService;
import com.backend.immilog.post.model.dtos.PostDTO;
import com.backend.immilog.post.model.enums.Categories;
import com.backend.immilog.post.model.enums.Countries;
import com.backend.immilog.post.model.enums.SortingMethods;
import com.backend.immilog.post.presentation.request.PostUpdateRequest;
import com.backend.immilog.post.presentation.request.PostUploadRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    @PostMapping
    @ExtractUserId
    @ApiOperation(value = "게시물 작성", notes = "게시물을 작성합니다.")
    public ResponseEntity<ApiResponse> createPost(
            HttpServletRequest request,
            @Valid @RequestBody PostUploadRequest postUploadRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postUploadService.uploadPost(userSeq, postUploadRequest);
        return ResponseEntity.status(CREATED).build();
    }

    @PatchMapping("/{postSeq}")
    @ExtractUserId
    @ApiOperation(value = "게시물 수정", notes = "게시물을 수정합니다.")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postSeq,
            HttpServletRequest request,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postUpdateService.updatePost(userSeq, postSeq, postUpdateRequest);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PatchMapping("/{postSeq}/delete")
    @ExtractUserId
    @ApiOperation(value = "게시물 삭제", notes = "게시물을 삭제합니다.")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postSeq,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postDeleteService.deletePost(userSeq, postSeq);
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
    @ExtractUserId
    @ApiOperation(value = "게시물 좋아요", notes = "게시물에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likePost(
            @PathVariable Long postSeq,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        postUpdateService.likePost(userSeq, postSeq);
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

    @GetMapping("/{postSeq}")
    @ApiOperation(value = "게시물 상세 조회", notes = "게시물 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse> getPost(
            @PathVariable Long postSeq
    ) {
        PostDTO post = postInquiryService.getPost(postSeq);
        return ResponseEntity
                .status(OK)
                .body(ApiResponse.of(post));
    }

    @GetMapping("/search")
    @ApiOperation(value = "게시물 검색", notes = "게시물을 검색합니다.")
    public ResponseEntity<ApiResponse> searchPosts(
            @RequestParam(required = true) String keyword,
            @RequestParam(required = true) Integer page
    ) {
        Page<PostDTO> posts = postInquiryService.searchKeyword(keyword, page);
        return ResponseEntity.status(OK).body(ApiResponse.of(posts));
    }

    @GetMapping("/users/{userSeq}/page/{page}")
    @ApiOperation(value = "사용자 본인의 게시물 목록 조회", notes = "사용자 게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getUserPosts(
            @PathVariable Long userSeq,
            @PathVariable Integer page
    ) {
        Page<PostDTO> posts = postInquiryService.getUserPosts(userSeq, page);
        return ResponseEntity.status(OK).body(ApiResponse.of(posts));
    }

}
