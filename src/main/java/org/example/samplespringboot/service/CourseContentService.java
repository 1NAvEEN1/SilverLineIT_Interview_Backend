package org.example.samplespringboot.service;

import org.example.samplespringboot.dto.CourseContentResponseDTO;
import org.example.samplespringboot.entity.CourseContent;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseContentService {
    CourseContentResponseDTO uploadFile(MultipartFile file, Long courseId, Long userId);
    CourseContentResponseDTO getContentById(Long id);
    List<CourseContentResponseDTO> getContentsByCourseId(Long courseId);
    List<CourseContentResponseDTO> getContentsByUserId(Long userId);

    CourseContent uploadDocument(MultipartFile multipartFile,String fileName, String documentDir);
}

