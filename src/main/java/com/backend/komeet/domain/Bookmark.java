package com.backend.komeet.domain;

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
    private List<Post> bookmarkPosts;

}
