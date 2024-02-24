package com.backend.komeet.dto.request;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatContentRequest {
    private String content;
    private List<String> attachments;
}
