package com.backend.immilog.global.application;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.backend.immilog.global.exception.CustomException;
import com.backend.immilog.global.model.dtos.ImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.backend.immilog.global.exception.ErrorCode.IMAGE_UPLOAD_FAILED;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageDTO saveFiles(
            List<MultipartFile> multipartFiles,
            String imagePath
    ) {
        List<String> imageUrls = multipartFiles.stream()
                .map(multipartFile -> saveFileAndGetUrl(multipartFile, imagePath))
                .toList();

        return ImageDTO.from(imageUrls);
    }

    private String saveFileAndGetUrl(
            MultipartFile multipartFile,
            String imagePath
    ) {
        String originalFileName = generateFileName(multipartFile, imagePath);
        uploadFileToS3(multipartFile, originalFileName);
        return generateFileUrl(originalFileName);
    }

    private String generateFileName(
            MultipartFile multipartFile,
            String imagePath
    ) {
        return imagePath + "/" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                + "." + getFileExtension(multipartFile);
    }

    private void uploadFileToS3(
            MultipartFile multipartFile,
            String originalFileName
    ) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(
                    bucket,
                    originalFileName,
                    multipartFile.getInputStream(),
                    metadata
            );
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }
    }

    private String generateFileUrl(
            String originalFileName
    ) {
        return amazonS3.getUrl(bucket, originalFileName).toString();
    }

    private String getFileExtension(
            MultipartFile file
    ) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return originalFilename.substring(lastDotIndex + 1);
            }
        }
        return "png"; // 확장자가 없는 경우 png 반환
    }
}
