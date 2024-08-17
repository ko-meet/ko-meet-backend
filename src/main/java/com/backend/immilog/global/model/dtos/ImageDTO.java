package com.backend.immilog.global.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDTO {

    private List<String> imageUrl;

    public static ImageDTO from(
            List<String> imageUrl
    ) {
        return ImageDTO.builder()
                .imageUrl(imageUrl)
                .build();
    }
}

