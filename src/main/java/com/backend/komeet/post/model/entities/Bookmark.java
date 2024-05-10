package com.backend.komeet.post.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Bookmark extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkSeq;
    private Long userSeq;

    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("createdAt DESC")
    private List<Post> bookmarkPosts;

}
