package com.backend.komeet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
