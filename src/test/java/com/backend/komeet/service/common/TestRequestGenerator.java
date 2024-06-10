package com.backend.komeet.service.common;

import com.backend.komeet.notice.application.NoticeModifyService;
import com.backend.komeet.notice.presentation.request.NoticeModifyRequest;
import com.backend.komeet.notice.presentation.request.NoticeRegisterRequest;
import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.post.presentation.request.PostUploadRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.backend.komeet.notice.enums.NoticeType.NOTICE;
import static com.backend.komeet.user.enums.Countries.SOUTH_KOREA;

public class TestRequestGenerator {

    public static PostUploadRequest createPostUploadRequest() {
        return PostUploadRequest.builder()
                .title("제목")
                .content("내용")
                .attachments(List.of("test"))
                .tags(List.of("test"))
                .isPublic(true)
                .build();
    }

    public static PostUpdateRequest createPostUpdateRequest() {
        return PostUpdateRequest.builder()
                .title("새제목")
                .content("새내용")
                .isPublic(false)
                .build();
    }

    public static NoticeRegisterRequest createNoticeRegisterRequest() {
        return NoticeRegisterRequest.builder()
                .title("제목")
                .content("내용")
                .type(NOTICE)
                .targetCountries(new ArrayList<>(Arrays.asList(SOUTH_KOREA)))
                .build();
    }

    public static NoticeModifyRequest createNoticeModifyRequest() {
        return NoticeModifyRequest.builder()
                .title("새제목")
                .content("새내용")
                .type(NOTICE)
                .build();
    }
}
