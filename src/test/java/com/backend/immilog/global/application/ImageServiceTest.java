package com.backend.immilog.global.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("이미지 서비스 테스트")
class ImageServiceTest {

    @Mock
    private FileStorageHandler fileStorageHandler;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService(fileStorageHandler);
    }

    @Test
    @DisplayName("이미지 업로드")
    void uploadImage() {
        // given
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        String imagePath = "imagePath";
        String mockUrl = "https://example.com/path";

        when(fileStorageHandler.uploadFile(any(MultipartFile.class), eq(imagePath)))
                .thenReturn(mockUrl);

        // when
        List<String> images = imageService.saveFiles(files, imagePath);

        // then
        assertThat(images).isNotEmpty();
        assertThat(images.get(0)).isEqualTo(mockUrl);

        verify(fileStorageHandler, times(1)).uploadFile(any(MultipartFile.class), eq(imagePath));
    }

    @Test
    @DisplayName("이미지 삭제")
    void deleteImage() {
        // given
        String imagePath = "imagePath";

        // when
        imageService.deleteFile(imagePath);

        // then
        verify(fileStorageHandler, times(1)).deleteFile(eq(imagePath));
    }
}
