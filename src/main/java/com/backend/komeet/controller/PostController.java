package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.SearchResultDto;
import com.backend.komeet.dto.request.PostUpdateRequest;
import com.backend.komeet.dto.request.PostUploadRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.Countries;
import com.backend.komeet.enums.SortingMethods;
import com.backend.komeet.service.post.PostDeleteService;
import com.backend.komeet.service.post.PostLikeService;
import com.backend.komeet.service.post.PostUpdateService;
import com.backend.komeet.service.post.PostUploadService;
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

    @PatchMapping("/{postSeq}/like")
    @ApiOperation(value = "게시물 좋아요", notes = "게시물에 좋아요를 누릅니다.")
    public ResponseEntity<ApiResponse> likePost(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userId = jwtProvider.getIdFromToken(token);

        postLikeService.likePost(userId, postSeq);

        return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(NO_CONTENT.value()));
    }

    @GetMapping
    @ApiOperation(value = "게시물 목록 조회", notes = "게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getPosts(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String sortingMethod,
            @RequestParam(required = false) String isPublic,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page) {

        return ResponseEntity.ok().body(new ApiResponse(postUploadService.getPosts(
                Countries.getCountry(country),
                SortingMethods.valueOf(sortingMethod),
                isPublic,
                Categories.valueOf(category),
                page == null ? 0 : page
        )));
    }

    @PatchMapping("/{postSeq}/view")
    @ApiOperation(value = "게시물 조회수 증가", notes = "게시물 조회수를 증가시킵니다.")
    public ResponseEntity<ApiResponse> increaseViewCount(
            @PathVariable Long postSeq) {

        postUpdateService.increaseViewCount(postSeq);

        return ResponseEntity.status(NO_CONTENT).body(new ApiResponse(NO_CONTENT.value()));
    }

    @GetMapping("/{postSeq}")
    @ApiOperation(value = "게시물 상세 조회", notes = "게시물 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse> getPost(
            @PathVariable Long postSeq) {

        return ResponseEntity.status(OK)
                .body(new ApiResponse(postUploadService.getPost(postSeq)));
    }

    @GetMapping("/search")
    @ApiOperation(value = "게시물 검색", notes = "게시물을 검색합니다.")
    public ResponseEntity<ApiResponse> searchPosts(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam(required = true) String keyword,
            @RequestParam(required = true) Integer page) {
        Page<SearchResultDto> list =
                postUploadService.searchKeyword(keyword, page);
        return ResponseEntity.status(OK).body(new ApiResponse(list));
    }

    @GetMapping("/my")
    @ApiOperation(value = "내 게시물 목록 조회", notes = "내 게시물 목록을 조회합니다.")
    public ResponseEntity<ApiResponse> getMyPosts(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam(required = true) Integer page) {

        Long userId = jwtProvider.getIdFromToken(token);

        return ResponseEntity.ok().body(
                new ApiResponse(postUploadService.getMyPosts(userId, page))
        );
    }
}
