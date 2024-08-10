package com.backend.komeet.post.application.bookmark;

import com.backend.komeet.global.exception.CustomException;
import com.backend.komeet.post.enums.PostType;
import com.backend.komeet.post.model.dtos.JobBoardDto;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.post.model.entities.Bookmark;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.repositories.BookmarkRepository;
import com.backend.komeet.post.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.backend.komeet.global.exception.ErrorCode.POST_NOT_FOUND;

/**
 * 북마크 생성 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkCreationService {
    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    /**
     * 북마크를 생성하는 메서드
     */
    @Transactional
    public void createPostBookmark(
            Long userSeq,
            Long postSeq,
            PostType postType
    ) {
        AtomicBoolean isExist = new AtomicBoolean(true);
        Bookmark bookmark = getBookmark(userSeq, postType, isExist);
        Post post = getPost(postSeq);
        addOrRemoveBookmarkPost(post, bookmark, userSeq);
        post.getPostMetaData().getBookmarkUsers().add(userSeq);
        saveBookmarkIfItsNewBookmark(isExist, bookmark);
    }

    /**
     * 북마크 목록을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public List<?> getBookmarkList(
            Long userSeq,
            PostType postType
    ) {
        return switch (postType) {
            case POST -> getPostsBookmarked(userSeq);
            case JOB_BOARD -> getJobBoardsBookmarked(userSeq);
        };
    }

    /**
     * 게시물을 조회하는 메서드
     */
    private Post getPost(
            Long postSeq
    ) {
        return postRepository
                .getPostWithBookmarkList(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    /**
     * 북마크를 조회하는 메서드
     */
    private Bookmark getBookmark(
            Long userSeq,
            PostType postType,
            AtomicBoolean isExist
    ) {
        return bookmarkRepository.findByUserSeq(userSeq)
                .orElseGet(() -> {
                    isExist.set(false);
                    return Bookmark.from(userSeq, postType);
                });
    }

    /**
     * 북마크에 게시물을 추가하거나 삭제하는 메서드
     */
    private static void addOrRemoveBookmarkPost(
            Post post,
            Bookmark bookmark,
            Long userSeq
    ) {
        if (post.getPostMetaData().getBookmarkUsers().contains(userSeq)) {
            post.removeBookmarkPost(bookmark, userSeq);
        } else {
            post.addBookmarkPost(bookmark, userSeq);
        }
    }

    /**
     * 북마크를 저장하는 메서드
     */
    private void saveBookmarkIfItsNewBookmark(
            AtomicBoolean isExist,
            Bookmark bookmark
    ) {
        if (!isExist.get()) {
            bookmarkRepository.save(bookmark);
        }
    }

    /**
     * 북마크된 게시물 목록을 조회하는 메서드
     */
    private List<JobBoardDto> getJobBoardsBookmarked(Long userSeq) {
        return bookmarkRepository
                .getBookmarkJobBoardsByUserSeq(userSeq)
                .stream()
                .map(JobBoardDto::from)
                .toList();
    }

    /**
     * 북마크된 구인공고 목록을 조회하는 메서드
     */
    private List<PostDto> getPostsBookmarked(Long userSeq) {
        return bookmarkRepository
                .getBookmarkPostsByUserSeq(userSeq)
                .stream()
                .map(PostDto::from)
                .toList();
    }

}