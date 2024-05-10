package com.backend.komeet.base.presentation.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이미지 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "ImageRequest", description = "이미지 요청 DTO")
public class ImageRequest {
    private String imageDirectory;
    private String imagePath;
}
