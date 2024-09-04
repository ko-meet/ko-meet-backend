package com.backend.immilog.global.application;

import com.backend.immilog.global.infrastructure.storage.FileStorageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ImageService {
    private final FileStorageHandler fileStorageHandler;

    public List<String> saveFiles(
            List<MultipartFile> multipartFiles,
            String imagePath
    ) {
        return multipartFiles.stream()
                .map(multipartFile -> fileStorageHandler.uploadFile(multipartFile, imagePath))
                .toList();
    }

    public void deleteFile(String imagePath) {
        fileStorageHandler.deleteFile(imagePath);
    }
}
