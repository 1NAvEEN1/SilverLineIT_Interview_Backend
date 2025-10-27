package org.example.samplespringboot.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.CourseRequestDTO;
import org.example.samplespringboot.dto.CourseResponseDTO;
import org.example.samplespringboot.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    /**
     * Create a new course with optional file uploads
     * POST /api/courses
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> createCourse(
            @RequestPart("course") @Valid CourseRequestDTO courseRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        CourseResponseDTO createdCourse = courseService.createCourseWithContent(courseRequestDTO, files);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * Get a single course with all its content (non-deleted)
     * GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        CourseResponseDTO course = courseService.getCourseByIdWithContents(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Get all courses by instructor ID
     * GET /api/courses/instructor/{instructorId}
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByInstructorId(@PathVariable Long instructorId) {
        List<CourseResponseDTO> courses = courseService.getCoursesByInstructorId(instructorId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Update course with optional new file uploads
     * PUT /api/courses/{id}
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @RequestPart("course") @Valid CourseRequestDTO courseRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        CourseResponseDTO updatedCourse = courseService.updateCourseWithContent(id, courseRequestDTO, files);
        return ResponseEntity.ok(updatedCourse);
    }
}

