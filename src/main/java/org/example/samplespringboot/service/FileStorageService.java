package org.example.samplespringboot.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    String storeFile(MultipartFile file, Long courseId);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
    Path getFileStorageLocation();
}

