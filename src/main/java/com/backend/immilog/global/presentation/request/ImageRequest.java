package com.backend.immilog.global.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.Builder;

@Builder
@ApiModel(value = "ImageRequest", description = "이미지 요청 DTO")
public record ImageRequest(
        String imageDirectory,
        String imagePath
) {
}

