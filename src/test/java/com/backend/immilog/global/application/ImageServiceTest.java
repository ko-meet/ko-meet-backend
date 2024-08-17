package com.backend.immilog.global.application;

import com.amazonaws.services.s3.AmazonS3;
import com.backend.immilog.global.model.dtos.ImageDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("이미지 서비스 테스트")
class ImageServiceTest {
    @Mock
    private AmazonS3 amazonS3;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        imageService = new ImageService(amazonS3);
        ReflectionTestUtils.setField(imageService, "bucket", "immilog");
    }

    @Test
    @DisplayName("이미지 S3 업로드")
    void uploadImageToS3() throws MalformedURLException {
        // given
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        String imagePath = "imagePath";
        String url = "https://example.com/path";
        when(amazonS3.getUrl(anyString(), anyString()))
                .thenReturn(new URL(url));
        // when
        ImageDTO dto = imageService.saveFiles(files, imagePath);
        // then
        assertThat(dto.getImageUrl().get(0)).isEqualTo(url);
    }

    @Test
    @DisplayName("이미지 S3 삭제")
    void deleteImageFromS3() {
        // given
        String imagePath = "imagePath";
        // when
        imageService.deleteFile(imagePath);
        // then
        verify(amazonS3, times(1))
                .deleteObject(anyString(), anyString());
    }
}
