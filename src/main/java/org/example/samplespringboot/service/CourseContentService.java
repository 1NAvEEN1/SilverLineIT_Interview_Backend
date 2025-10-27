package org.example.samplespringboot.service;

import org.example.samplespringboot.dto.CourseContentResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseContentService {
    CourseContentResponseDTO uploadFile(MultipartFile file, Long courseId, Long userId);
    CourseContentResponseDTO getContentById(Long id);
    List<CourseContentResponseDTO> getContentsByCourseId(Long courseId);
    List<CourseContentResponseDTO> getContentsByUserId(Long userId);
    Resource downloadFile(Long id);
    void deleteContent(Long id);
}

