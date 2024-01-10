package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.request.PostCreateRequest;
import com.backend.komeet.service.post.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * 게시물 관련 API를 정의한 컨트롤러
 */
@Api(tags = "Post API", description = "게시물 관련 API")
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;
    private final JwtProvider jwtProvider;

    @PostMapping
    @ApiOperation(value = "게시물 작성", notes = "게시물을 작성합니다.")
    public ResponseEntity<Void> createPost(
            @RequestHeader(AUTHORIZATION) String token,
            @Valid @RequestBody PostCreateRequest postCreateRequest) {

        Long userId = jwtProvider.getIdFromToken(token);

        postService.createPost(userId, postCreateRequest);

        return ResponseEntity.status(CREATED).build();
    }
}
