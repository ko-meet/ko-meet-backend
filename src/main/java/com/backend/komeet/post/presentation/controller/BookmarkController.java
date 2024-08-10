package com.backend.komeet.post.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.global.security.JwtProvider;
import com.backend.komeet.post.application.bookmark.BookmarkCreationService;
import com.backend.komeet.post.enums.PostType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * 북마크 관련 컨트롤러
 */
@Api(tags = "Bookmark API", description = "즐겨찾기 관련 API")
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkCreationService bookmarkCreationService;
    private final JwtProvider jwtProvider;

    /**
     * 북마크 등록
     */
    @PostMapping("/{postType}/{postSeq}")
    @ApiOperation(value = "북마크 등록", notes = "게시물을 북마크에 등록합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postSeq,
            @PathVariable String postType,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        bookmarkCreationService.createPostBookmark(
                userSeq,
                postSeq,
                PostType.convertToEnum(postType)
        );
        return ResponseEntity.status(CREATED).build();
    }

    /**
     * 북마크 조회
     */
    @GetMapping("/{postType}")
    @ApiOperation(value = "북마크 조회", notes = "사용자의 북마크를 조회합니다.")
    public ResponseEntity<ApiResponse> getBookmarks(
            @PathVariable String postType,
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(
                                bookmarkCreationService.getBookmarkList(
                                        userSeq,
                                        PostType.convertToEnum(postType)
                                )
                        )
                );
    }
}
