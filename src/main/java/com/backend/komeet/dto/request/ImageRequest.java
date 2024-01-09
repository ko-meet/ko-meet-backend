package com.backend.komeet.dto.request;

import com.backend.komeet.enums.ImageDirectory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageRequest {
    private ImageDirectory imageDirectory;
    private String imagePath;
}
