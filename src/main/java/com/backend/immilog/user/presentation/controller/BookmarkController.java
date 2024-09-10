package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.global.security.ExtractUserId;
import com.backend.immilog.user.application.services.BookmarkCreationService;
import com.backend.immilog.user.presentation.response.UserApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.CREATED;

@Api(tags = "Bookmark API", description = "즐겨찾기 관련 API")
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
@RestController
public class BookmarkController {

    private final BookmarkCreationService bookmarkCreationService;

    @ExtractUserId
    @PostMapping("/{postType}/{postSeq}")
    @ApiOperation(value = "북마크 등록", notes = "게시물을 북마크에 등록합니다.")
    public ResponseEntity<UserApiResponse> createComment(
            @PathVariable Long postSeq,
            @PathVariable String postType,
            HttpServletRequest request
    ) {
        Long userSeq = (Long) request.getAttribute("userSeq");
        bookmarkCreationService.createBookmark(
                userSeq,
                postSeq,
                postType
        );
        return ResponseEntity.status(CREATED).build();
    }
}
