package com.backend.komeet.service.common;

import com.backend.komeet.company.model.entities.Company;
import com.backend.komeet.notice.model.entities.Notice;
import com.backend.komeet.post.enums.Experience;
import com.backend.komeet.post.enums.Industry;
import com.backend.komeet.post.model.entities.Bookmark;
import com.backend.komeet.post.model.entities.JobBoard;
import com.backend.komeet.post.model.entities.Post;
import com.backend.komeet.post.model.entities.metadata.CompanyMetaData;
import com.backend.komeet.post.model.entities.metadata.PostMetaData;
import com.backend.komeet.user.model.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.backend.komeet.notice.enums.NoticeType.NOTICE;
import static com.backend.komeet.post.enums.PostStatus.DELETED;
import static com.backend.komeet.post.enums.PostStatus.NORMAL;
import static com.backend.komeet.user.enums.Countries.LAOS;
import static com.backend.komeet.user.enums.Countries.SOUTH_KOREA;
import static com.backend.komeet.user.enums.UserRole.ROLE_ADMIN;
import static com.backend.komeet.user.enums.UserRole.ROLE_USER;

public class TestEntityGenerator {

    public static User user = User.builder()
            .seq(1L)
            .email("test@test.test")
            .password("test")
            .country(SOUTH_KOREA)
            .userRole(ROLE_USER)
            .build();

    public static User admin = User.builder()
            .seq(2L)
            .email("test2@test2.test")
            .password("test2")
            .country(SOUTH_KOREA)
            .userRole(ROLE_ADMIN)
            .build();

    public static Post postNormal = Post.builder()
            .seq(1L)
            .postMetaData(
                    PostMetaData.builder()
                            .title("제목")
                            .content("내용")
                            .attachments(List.of("첨부파일1", "첨부파일2"))
                            .tags(List.of("태그1", "태그2"))
                            .country(LAOS)
                            .region("지역")
                            .bookmarkUsers(new ArrayList<>(Arrays.asList(1L)))
                            .status(NORMAL)
                            .build()
            )
            .isPublic("Y")
            .user(user)
            .comments(List.of())
            .build();

    public static Post postDeleted = Post.builder()
            .seq(1L)
            .postMetaData(
                    PostMetaData.builder()
                            .title("제목")
                            .content("내용")
                            .attachments(List.of("첨부파일1", "첨부파일2"))
                            .tags(List.of("태그1", "태그2"))
                            .country(LAOS)
                            .region("지역")
                            .bookmarkUsers(new ArrayList<>(List.of(1L)))
                            .status(DELETED)
                            .build()
            )
            .isPublic("Y")
            .user(user)
            .comments(List.of())
            .build();

    public static Bookmark bookmark = Bookmark.builder()
            .bookmarkSeq(1L)
            .userSeq(1L)
            .build();

    public static Notice notice = Notice.builder()
            .seq(1L)
            .authorUserSeq(1L)
            .title("제목")
            .content("내용")
            .type(NOTICE)
            .status(NORMAL)
            .targetCountries(new ArrayList<>(Arrays.asList(SOUTH_KOREA)))
            .readUsers(new ArrayList<>(Arrays.asList(1L)))
            .build();
    public static JobBoard jobBoard = JobBoard.builder()
            .seq(1L)
            .postMetaData(
                    PostMetaData.builder()
                            .title("제목")
                            .content("내용")
                            .attachments(List.of("첨부파일1", "첨부파일2"))
                            .tags(List.of("태그1", "태그2"))
                            .country(LAOS)
                            .region("지역")
                            .bookmarkUsers(new ArrayList<>(Arrays.asList(1L)))
                            .status(NORMAL)
                            .build()
            )
            .companyMetaData(
                    CompanyMetaData.builder()
                            .experience(Experience.ALL)
                            .industry(Industry.ALL)
                            .build()
            )
            .user(user)
            .build();

    public static Company company = Company.builder()
            .seq(1L)
            .industry(Industry.ALL)
            .companyName("회사이름")
            .companyEmail("회사이메일")
            .companyPhone("회사전화번호")
            .companyAddress("회사주소")
            .companyHomepage("회사홈페이지")
            .companyCountry(LAOS)
            .companyRegion("회사지역")
            .companyLogo("회사로고")
            .companyManagerUserSeq(1L)
            .build();
}
