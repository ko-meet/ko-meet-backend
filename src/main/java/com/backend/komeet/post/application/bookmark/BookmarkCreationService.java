package com.backend.komeet.post.application.bookmark;

import com.backend.komeet.post.model.entities.Bookmark;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.dtos.PostDto;
import com.backend.komeet.infrastructure.exception.CustomException;
import com.backend.komeet.post.repositories.BookmarkRepository;
import com.backend.komeet.post.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.backend.komeet.infrastructure.exception.ErrorCode.POST_NOT_FOUND;

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
    public void createBookmark(
            Long userSeq,
            Long postSeq
    ) {
        Bookmark bookmark = getBookmark(userSeq);
        Post post = getPost(postSeq);
        List<Post> bookmarkPosts = bookmark.getBookmarkPosts();
        if (bookmarkPosts.contains(post)) {
            bookmarkPosts.remove(post);
        } else {
            bookmarkPosts.add(post);
        }
        post.getBookmarkUsers().add(userSeq);
        bookmarkRepository.save(bookmark);
    }

    /**
     * 북마크 목록을 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public List<PostDto> getBookmarkList(
            Long userSeq
    ) {
        return getBookmark(userSeq)
                .getBookmarkPosts()
                .stream()
                .map(PostDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시물을 조회하는 메서드
     */
    private Post getPost(
            Long postSeq
    ) {
        return postRepository
                .findById(postSeq)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));
    }

    /**
     * 북마크를 조회하는 메서드
     */
    private Bookmark getBookmark(
            Long userSeq
    ) {
        Bookmark bookmark = bookmarkRepository.findByUserSeq(userSeq).orElseGet(
                () -> bookmarkRepository.save(
                        Bookmark.builder()
                                .userSeq(userSeq)
                                .bookmarkPosts(new ArrayList<>())
                                .build()
                )
        );
        log.error("bookmark : {}", bookmark.getBookmarkPosts());
        return bookmark;
    }

}