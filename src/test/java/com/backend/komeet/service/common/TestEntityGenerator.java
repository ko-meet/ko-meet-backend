package com.backend.komeet.service.common;


import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.user.model.entities.User;
import com.backend.komeet.post.model.entities.Bookmark;


import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.post.enums.PostStatus.NORMAL;
import static com.backend.komeet.user.enums.Countries.LAOS;
import static com.backend.komeet.user.enums.Countries.SOUTH_KOREA;

public class TestEntityGenerator {

    public static User user = User.builder()
            .seq(1L)
            .email("test@test.test")
            .password("test")
            .country(SOUTH_KOREA)
            .build();

    public static Post postNormal = Post.builder()
            .seq(1L)
            .title("제목")
            .attachments(List.of("첨부파일1", "첨부파일2"))
            .tags(List.of("태그1", "태그2"))
            .isPublic("Y")
            .country(LAOS)
            .region("지역")
            .bookmarkUsers(new ArrayList<>(Arrays.asList(1L)))
            .user(user)
            .content("내용")
            .comments(List.of())
            .status(NORMAL)
            .build();

    public static Post postDeleted = Post.builder()
            .seq(1L)
            .title("제목")
            .attachments(List.of("첨부파일1", "첨부파일2"))
            .tags(List.of("태그1", "태그2"))
            .isPublic("Y")
            .country(LAOS)
            .region("지역")
            .bookmarkUsers(new ArrayList<>(Arrays.asList(1L)))
            .user(user)
            .content("내용")
            .comments(List.of())
            .status(DELETED)
            .build();

    public static Bookmark bookmark = Bookmark.builder()
            .bookmarkSeq(1L)
            .bookmarkPosts(new ArrayList<>(Arrays.asList(postNormal)))
            .userSeq(1L)
            .build();

}
