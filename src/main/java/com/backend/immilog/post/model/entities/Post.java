package com.backend.immilog.post.model.entities;

import com.backend.immilog.global.model.BaseDateEntity;
import com.backend.immilog.post.application.command.PostUploadCommand;
import com.backend.immilog.post.model.embeddables.PostMetaData;
import com.backend.immilog.post.model.embeddables.PostUserData;
import com.backend.immilog.post.model.enums.Categories;
import com.backend.immilog.post.model.enums.Countries;
import com.backend.immilog.user.model.entities.User;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicUpdate
@Entity
public class Post extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Embedded
    private PostUserData postUserData;

    @Embedded
    private PostMetaData postMetaData;

    @Enumerated(EnumType.STRING)
    private Categories category;

    @Setter
    private String isPublic;

    @Setter
    private Long commentCount;

    public static Post of(
            PostUploadCommand postUploadCommand,
            User user
    ) {
        PostMetaData postMetaData = PostMetaData.of(
                postUploadCommand,
                Countries.valueOf(user.getLocation().getCountry().toString()),
                user.getLocation().getRegion()
        );

        PostUserData postUserData = PostUserData.builder()
                .userSeq(user.getSeq())
                .profileImage(user.getImageUrl())
                .nickname(user.getNickName())
                .build();

        return Post.builder()
                .postUserData(postUserData)
                .postMetaData(postMetaData)
                .category(postUploadCommand.category())
                .isPublic(postUploadCommand.isPublic() ? "Y" : "N")
                .commentCount(0L)
                .build();
    }
}
