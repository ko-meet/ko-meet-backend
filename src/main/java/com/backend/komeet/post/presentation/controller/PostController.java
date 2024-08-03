package com.backend.komeet.post.presentation.controller;

import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.post.model.dtos.SearchResultDto;
import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.post.presentation.request.PostUploadRequest;
import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.user.enums.Countries;
import com.backend.komeet.post.enums.SortingMethods;
import com.backend.komeet.post.application.post.PostDeleteService;
import com.backend.komeet.post.application.post.PostLikeService;
import com.backend.komeet.post.application.post.PostUpdateService;
import com.backend.komeet.post.application.post.PostUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

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
    private final PostLikeService postLikeService;
    private final JwtProvider jwtProvider;

    /**
     * 게시물 작성
     */
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

    /**
     * 게시물 수정
     */
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

    /**
     * 게시물 삭제
     */
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

    /**
     * 게시물 좋아요
     */
    @PatchMapping("/{postSeq}/like")
    @ApiOperation(value = "게시물 좋아요", notes = "게시물에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userId = jwtProvider.getIdFromToken(token);
        postLikeService.likePost(userId, postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 게시물 목록 조회
     */
    @GetMapping
    @ApiOperation(value = "게시물 목록 조회", notes = "게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getPosts(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortingMethod,
            @RequestParam(required = false) String isPublic,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page
    ) {
        return ResponseEntity.status(OK).body(
                new ApiResponse(
                        postUploadService.getPosts(
                                Countries.getCountry(country),
                                SortingMethods.valueOf(sortingMethod),
                                isPublic,
                                Categories.valueOf(category),
                                page == null ? 0 : page
                        )
                )
        );
    }

    /**
     * 게시물 조회수 증가
     */
    @PatchMapping("/{postSeq}/view")
    @ApiOperation(value = "게시물 조회수 증가", notes = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<ApiResponse> increaseViewCount(
            @PathVariable Long postSeq
    ) {
        postUpdateService.increaseViewCount(postSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 게시물 상세 조회
     */
    @GetMapping("/{postSeq}")
    @ApiOperation(value = "게시물 상세 조회", notes = "게시물 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse> getPost(
            @PathVariable Long postSeq
    ) {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(postUploadService.getPost(postSeq)));
    }

    /**
     * 게시물 검색
     */
    @GetMapping("/search")
    @ApiOperation(value = "게시물 검색", notes = "게시물을 검색합니다.")
    public ResponseEntity<ApiResponse> searchPosts(
            @RequestParam(required = true) String keyword,
            @RequestParam(required = true) Integer page
    ) {
        Page<SearchResultDto> list =
                postUploadService.searchKeyword(keyword, page);
        return ResponseEntity.status(OK).body(new ApiResponse(list));
    }

    /**
     * 사용자 게시물 목록 조회
     */
    @GetMapping("/users/{userSeq}/page/{page}")
    @ApiOperation(value = "사용자의 게시물 목록 조회", notes = "사용자 게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getUserPosts(
            @PathVariable Long userSeq,
            @PathVariable Integer page
    ) {
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(postUploadService.getUserPosts(userSeq, page)));
    }
}
