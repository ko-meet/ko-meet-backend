package com.backend.komeet.controller;

import com.backend.komeet.dto.ImageDto;
import com.backend.komeet.dto.request.ImageRequest;
import com.backend.komeet.enums.ImageDirectory;
import com.backend.komeet.service.external.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * 이미지 업로드 관련 API를 정의한 컨트롤러
 */
@Api(tags = "Imgae API", description = "이미지 업로드 관련 API")
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @ApiOperation(value = "이미지 업로드", notes = "이미지를 업로드합니다.")
    public ResponseEntity<String> uploadImage(MultipartFile multipartFile,
                                              ImageRequest imageRequest) {
        ImageDto imageDto = imageService.saveFile(multipartFile, imageRequest.getImageDirectory());
        return ResponseEntity.status(OK).body(imageDto.getImageUrl());
    }

    @DeleteMapping
    @ApiOperation(value = "이미지 삭제", notes = "이미지를 삭제합니다.")
    public ResponseEntity<Void> deleteImage(ImageRequest imageRequest) {
        imageService.deleteFile(imageRequest.getImagePath());
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
