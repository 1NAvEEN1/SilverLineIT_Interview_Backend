package org.example.samplespringboot.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.entity.CourseContent;
import org.example.samplespringboot.repository.CourseContentRepository;
import org.example.samplespringboot.repository.CourseRepository;
import org.example.samplespringboot.repository.UserRepository;
import org.example.samplespringboot.service.CourseContentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseContentServiceImpl implements CourseContentService {

    private final CourseContentRepository courseContentRepository;

    @Value("${file.max-size:10485760}") // Default 10MB
    private Long maxFileSize;

    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "application/pdf",
            "video/mp4",
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".pdf", ".mp4", ".jpg", ".jpeg", ".png"
    );


    @Override
    public CourseContent uploadDocument(MultipartFile submissions,String fileName,String documentDir) {

        if (submissions.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds the limit of 10MB.");
        }

        String contentType = submissions.getContentType();

        if (!ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new RuntimeException("Invalid file type. Allowed: PDF, MP4, JPG, PNG");
        }

        CourseContent courseContent = null;
        try {
            final Path root = Paths.get(documentDir);

            boolean directory = Files.isDirectory(root);
            if (!directory) {
                Files.createDirectory(root);
            }

            courseContent = new CourseContent();
            courseContent.setFileName(fileName);
            courseContent.setFilePath(documentDir);
            courseContent.setFileType(contentType);
            courseContent.setFileSize(submissions.getSize());

            courseContent = courseContentRepository.save(courseContent);


        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }

        return courseContent;
    }
}
