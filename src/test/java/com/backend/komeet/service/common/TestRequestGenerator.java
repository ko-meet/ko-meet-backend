package com.backend.komeet.service.common;

import com.backend.komeet.post.presentation.request.PostUpdateRequest;
import com.backend.komeet.post.presentation.request.PostUploadRequest;

import java.util.List;

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
}
