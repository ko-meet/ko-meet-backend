package com.backend.immilog.post.application.services;

import com.backend.immilog.post.domain.model.Bookmark;
import com.backend.immilog.post.domain.model.enums.PostType;
import com.backend.immilog.post.domain.repositories.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkCreationService {
    private final BookmarkRepository bookmarkRepository;

    public void createBookmark(
            Long userSeq,
            Long postSeq,
            String bookmarkTypeString
    ) {
        PostType bookmarkType = PostType.convertToEnum(bookmarkTypeString);
        Bookmark bookmark = Bookmark.of(userSeq, postSeq, bookmarkType);
        bookmarkRepository.saveEntity(bookmark);
    }
}
