package com.backend.komeet.service.external;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.backend.komeet.dto.ImageDto;
import com.backend.komeet.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.backend.komeet.exception.ErrorCode.IMAGE_UPLOAD_FAILED;

/**
 * 이미지 업로드, 삭제를 위한 서비스
 */
@RequiredArgsConstructor
@Service
public class ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일 업로드
     *
     * @param multipartFile  파일
     * @param imagePath 이미지 타입에 대한 경로
     * @return 업로드한 파일의 URL
     */
    public ImageDto saveFile(MultipartFile multipartFile,
                             String imagePath) {

        // 파일 이름에 이미지 타입에 대한 경로 및 UUID와 원래 파일의 확장자를 포함
        String originalFileName = imagePath + "/" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                + "." + getFileExtension(multipartFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // 파일 업로드
        try {
            amazonS3.putObject(bucket, originalFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new CustomException(IMAGE_UPLOAD_FAILED);
        }

        // 업로드한 파일의 URL 반환
        return ImageDto.builder()
                .imageUrl(amazonS3.getUrl(bucket, originalFileName).toString())
                .build();
    }

    /**
     * 파일 삭제
     *
     * @param imagePath 파일 경로
     */
    public void deleteFile(String imagePath) {
        // 파일 삭제
        amazonS3.deleteObject(bucket, imagePath);
    }

    /**
     * 파일 이름에서 확장자 추출
     *
     * @param file 파일
     * @return 파일 확장자
     */
    private String getFileExtension(MultipartFile file) {
        // 파일 이름에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex != -1) {
                return originalFilename.substring(lastDotIndex + 1);
            }
        }
        return ""; // 확장자가 없는 경우 빈 문자열 반환
    }
}
