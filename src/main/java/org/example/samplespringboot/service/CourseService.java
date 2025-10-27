package org.example.samplespringboot.service;

import org.example.samplespringboot.dto.CourseRequestDTO;
import org.example.samplespringboot.dto.CourseResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO);
    CourseResponseDTO createCourseWithContent(CourseRequestDTO courseRequestDTO, List<MultipartFile> files);
    CourseResponseDTO getCourseById(Long id);
    CourseResponseDTO getCourseByIdWithContents(Long id);
    List<CourseResponseDTO> getAllCourses();
    List<CourseResponseDTO> getCoursesByInstructorId(Long instructorId);
    CourseResponseDTO updateCourse(Long id, CourseRequestDTO courseRequestDTO);
    CourseResponseDTO updateCourseWithContent(Long id, CourseRequestDTO courseRequestDTO, List<MultipartFile> files);
    void deleteCourse(Long id);
}

