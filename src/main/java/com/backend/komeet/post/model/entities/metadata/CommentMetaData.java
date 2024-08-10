package com.backend.komeet.post.model.entities.metadata;

import com.backend.komeet.post.enums.PostStatus;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CommentMetaData {

    private Integer upVotes = 0;

    private Integer downVotes = 0;

    private String content;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> likeUsers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PostStatus status;
}