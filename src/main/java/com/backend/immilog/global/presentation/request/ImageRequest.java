package com.backend.immilog.global.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이미지 요청 DTO")
public record ImageRequest(
        String imageDirectory,
        String imagePath
) {
}

