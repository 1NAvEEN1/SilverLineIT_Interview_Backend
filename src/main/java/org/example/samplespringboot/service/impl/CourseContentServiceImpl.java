package org.example.samplespringboot.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.CourseContentResponseDTO;
import org.example.samplespringboot.entity.Course;
import org.example.samplespringboot.entity.CourseContent;
import org.example.samplespringboot.entity.User;
import org.example.samplespringboot.exception.FileSizeLimitExceededException;
import org.example.samplespringboot.exception.InvalidFileTypeException;
import org.example.samplespringboot.exception.ResourceNotFoundException;
import org.example.samplespringboot.repository.CourseContentRepository;
import org.example.samplespringboot.repository.CourseRepository;
import org.example.samplespringboot.repository.UserRepository;
import org.example.samplespringboot.service.CourseContentService;
import org.example.samplespringboot.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseContentServiceImpl implements CourseContentService {

    private final CourseContentRepository courseContentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

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
    @Transactional
    public CourseContentResponseDTO uploadFile(MultipartFile file, Long courseId, Long userId) {
        // Validate file
        validateFile(file);

        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Store file
        String fileName = fileStorageService.storeFile(file, courseId);

        // Create course content entity
        CourseContent courseContent = new CourseContent();
        courseContent.setFileName(file.getOriginalFilename());
        courseContent.setFileType(file.getContentType());
        courseContent.setFileSize(file.getSize());
        courseContent.setFileUrl(fileName);
        courseContent.setCourse(course);
        courseContent.setUploadedBy(user);

        // Save to database
        CourseContent savedContent = courseContentRepository.save(courseContent);

        return mapToDTO(savedContent);
    }

    @Override
    public CourseContentResponseDTO getContentById(Long id) {
        CourseContent content = courseContentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        return mapToDTO(content);
    }

    @Override
    public List<CourseContentResponseDTO> getContentsByCourseId(Long courseId) {
        List<CourseContent> contents = courseContentRepository.findByCourseId(courseId);
        return contents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseContentResponseDTO> getContentsByUserId(Long userId) {
        List<CourseContent> contents = courseContentRepository.findByUploadedById(userId);
        return contents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Resource downloadFile(Long id) {
        CourseContent content = courseContentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        return fileStorageService.loadFileAsResource(content.getFileUrl());
    }

    @Override
    @Transactional
    public void deleteContent(Long id) {
        CourseContent content = courseContentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // Delete file from storage
        fileStorageService.deleteFile(content.getFileUrl());

        // Delete from database
        courseContentRepository.delete(content);
    }

    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("Cannot upload empty file");
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new FileSizeLimitExceededException(
                    String.format("File size exceeds maximum limit of %d MB", maxFileSize / (1024 * 1024))
            );
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILE_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    "Invalid file type. Only PDF, MP4, JPG, JPEG, and PNG files are allowed"
            );
        }

        // Additional check for file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new InvalidFileTypeException(
                        "Invalid file extension. Only .pdf, .mp4, .jpg, .jpeg, and .png files are allowed"
                );
            }
        }
    }

    private CourseContentResponseDTO mapToDTO(CourseContent content) {
        CourseContentResponseDTO dto = new CourseContentResponseDTO();
        dto.setId(content.getId());
        dto.setFileName(content.getFileName());
        dto.setFileType(content.getFileType());
        dto.setFileSize(content.getFileSize());
        dto.setUploadDate(content.getUploadDate());
        dto.setFileUrl(content.getFileUrl());
        dto.setCourseId(content.getCourse().getId());
        dto.setCourseName(content.getCourse().getCourseName());
        dto.setUploadedByUserId(content.getUploadedBy().getId());
        dto.setUploadedByUserName(content.getUploadedBy().getFirstName() + " " + content.getUploadedBy().getLastName());
        return dto;
    }
}
