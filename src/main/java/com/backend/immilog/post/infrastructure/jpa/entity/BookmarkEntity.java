package com.backend.immilog.post.infrastructure.jpa.entity;

import com.backend.immilog.post.domain.model.Bookmark;
import com.backend.immilog.post.domain.model.enums.PostType;
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
    private PostType postType;

    private Long userSeq;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static BookmarkEntity from(
            Bookmark bookmark
    ) {
        LocalDateTime now = LocalDateTime.now();
        return BookmarkEntity.builder()
                .bookmarkSeq(bookmark.bookmarkSeq())
                .postType(bookmark.postType())
                .userSeq(bookmark.userSeq())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Bookmark toDomain() {
        return Bookmark.builder()
                .bookmarkSeq(bookmarkSeq)
                .postType(postType)
                .userSeq(userSeq)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}


