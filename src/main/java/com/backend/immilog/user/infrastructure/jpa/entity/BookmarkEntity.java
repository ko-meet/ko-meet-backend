package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.user.domain.model.Bookmark;
import com.backend.immilog.user.domain.model.enums.BookmarkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Entity
@Table(name = "bookmark")
public class BookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkSeq;

    @Enumerated(EnumType.STRING)
    private BookmarkType bookmarkType;

    private Long userSeq;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static BookmarkEntity from(
            Bookmark bookmark
    ) {
        LocalDateTime now = LocalDateTime.now();
        return BookmarkEntity.builder()
                .bookmarkSeq(bookmark.bookmarkSeq())
                .bookmarkType(bookmark.bookmarkType())
                .userSeq(bookmark.userSeq())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Bookmark toDomain() {
        return Bookmark.builder()
                .bookmarkSeq(bookmarkSeq)
                .bookmarkType(bookmarkType)
                .userSeq(userSeq)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}


