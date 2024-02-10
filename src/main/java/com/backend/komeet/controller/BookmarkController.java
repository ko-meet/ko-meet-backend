package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.PostDto;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.bookmark.BookmarkCreationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Bookmark API", description = "즐겨찾기 관련 API")
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@RestController
public class BookmarkController {
    private final BookmarkCreationService bookmarkCreationService;
    private final JwtProvider jwtProvider;

    @PostMapping("/posts/{postSeq}")
    @ApiOperation(value = "북마크 등록", notes = "게시물을 북마크에 등록합니다.")
    public ResponseEntity<ApiResponse> createComment(
            @PathVariable Long postSeq,
            @RequestHeader(AUTHORIZATION) String token) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        bookmarkCreationService.createBookmark(userSeq, postSeq);

        return ResponseEntity.status(CREATED).body(new ApiResponse(CREATED.value()));
    }

    @GetMapping
    @ApiOperation(value = "북마크 조회", notes = "사용자의 북마크를 조회합니다.")
    public ResponseEntity<ApiResponse> getBookmarks(
            @RequestHeader(AUTHORIZATION) String token) {

        Long userSeq = jwtProvider.getIdFromToken(token);

        List<PostDto> bookmarkList = bookmarkCreationService.getBookmarkList(userSeq);

        return ResponseEntity.status(OK).body(new ApiResponse(bookmarkList));
    }
}
