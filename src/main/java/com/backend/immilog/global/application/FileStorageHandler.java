package com.backend.immilog.global.application;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageHandler {
    String uploadFile(
            MultipartFile file,
            String imagePath
    );

    void deleteFile(
            String imagePath
    );
}
