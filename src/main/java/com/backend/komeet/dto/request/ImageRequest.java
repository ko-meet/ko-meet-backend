package com.backend.komeet.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageRequest {
    private String imageDirectory;
    private String imagePath;
}
