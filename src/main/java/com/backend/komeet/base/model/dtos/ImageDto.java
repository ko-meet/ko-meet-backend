package com.backend.komeet.base.model.dtos;

import lombok.*;

import java.util.List;

/**
 * 이미지 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {

    private List<String> imageUrl;

    public static ImageDto from(List<String> imageUrl) {
        return ImageDto.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
