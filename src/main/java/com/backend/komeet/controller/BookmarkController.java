package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.bookmark.BookmarkCreationService;
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
     *
     * @param postSeq 게시물 번호
     * @param token   토큰
     * @return {@link ResponseEntity<ApiResponse>} 북마크 등록 결과
     */
    @PostMapping("/posts/{postSeq}")
    @ApiOperation(value = "북마크 등록", notes = "게시물을 북마크에 등록합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        bookmarkCreationService.createBookmark(userSeq, postSeq);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }

    /**
     * 북마크 조회
     *
     * @param token 토큰
     * @return {@link ResponseEntity<ApiResponse>} 북마크 목록
     */
    @GetMapping
    @ApiOperation(value = "북마크 조회", notes = "사용자의 북마크를 조회합니다.")
    public ResponseEntity<ApiResponse> getBookmarks(
            @RequestHeader(AUTHORIZATION) String token) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        return ResponseEntity.status(OK).body(
                new ApiResponse(bookmarkCreationService.getBookmarkList(userSeq))
        );
    }
}
