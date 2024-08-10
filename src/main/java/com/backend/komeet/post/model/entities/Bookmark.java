package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.post.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Entity
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkSeq;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private Long userSeq;

    /**
     * 사용자 seq로 북마크 객체 생성
     */
    public static Bookmark from(
            Long userSeq,
            PostType postType
    ) {
        return Bookmark.builder()
                .userSeq(userSeq)
                .postType(postType)
                .build();
    }

}
