package org.example.samplespringboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.service.CourseContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course-content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseContentController {

    private final CourseContentService courseContentService;

    /**
     * Soft delete course content
     * DELETE /api/course-content/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        courseContentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}

