package com.backend.immilog.global.model.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record ImageDTO(
        List<String> imageUrl
) {
    public static ImageDTO from(
            List<String> imageUrl
    ) {
        return ImageDTO.builder()
                .imageUrl(imageUrl)
                .build();
    }
}

