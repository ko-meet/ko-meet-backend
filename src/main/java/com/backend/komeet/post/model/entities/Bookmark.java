package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private Long userSeq;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "bookmark_post",
//            joinColumns = @JoinColumn(name = "bookmark_seq"),
//            inverseJoinColumns = @JoinColumn(name = "post_seq")
//    )
//    @OrderBy("createdAt DESC")
//    private List<Post> bookmarkPosts = new ArrayList<>();

    /**
     * 사용자 seq로 북마크 객체 생성
     */
    public static Bookmark from(Long userSeq) {
        return Bookmark.builder()
                .userSeq(userSeq)
                .build();
    }
//
//    /**
//     * 북마크에 게시물 추가
//     */
//    public void addPost(Post post) {
//        bookmarkPosts.add(post);
//    }
//
//    /**
//     * 북마크에서 게시물 삭제
//     */
//    public void removePost(Post post) {
//        bookmarkPosts.remove(post);
//    }
}
