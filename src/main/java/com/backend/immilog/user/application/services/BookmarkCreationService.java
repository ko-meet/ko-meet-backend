package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.Bookmark;
import com.backend.immilog.user.domain.model.enums.BookmarkType;
import com.backend.immilog.user.domain.repositories.BookmarkRepository;
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
        BookmarkType bookmarkType = BookmarkType.convertToEnum(bookmarkTypeString);
        Bookmark bookmark = Bookmark.of(userSeq, postSeq, bookmarkType);
        bookmarkRepository.saveEntity(bookmark);
    }
}
