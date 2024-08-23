package com.backend.immilog.global.presentation.controller;

import com.backend.immilog.global.application.ImageService;
import com.backend.immilog.global.model.dtos.ImageDTO;
import com.backend.immilog.global.presentation.request.ImageRequest;
import com.backend.immilog.global.presentation.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Api(tags = "Imgae API", description = "이미지 업로드 관련 API")
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @ApiOperation(value = "이미지 업로드", notes = "이미지를 업로드합니다.")
    public ResponseEntity<ApiResponse> uploadImage(
            List<MultipartFile> multipartFile,
            @RequestParam String imagePath
    ) {
        ImageDTO data = imageService.saveFiles(multipartFile, imagePath);

        return ResponseEntity
                .status(OK)
                .body(ApiResponse.of(data));
    }

    @DeleteMapping
    @ApiOperation(value = "이미지 삭제", notes = "이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse> deleteImage(
            ImageRequest imageRequest
    ) {
        imageService.deleteFile(imageRequest.imagePath());
        return ResponseEntity.status(NO_CONTENT).build();
    }
}