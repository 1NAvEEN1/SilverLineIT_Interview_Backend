package org.example.samplespringboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.CourseContentResponseDTO;
import org.example.samplespringboot.dto.FileUploadResponseDTO;
import org.example.samplespringboot.service.CourseContentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/course-content")
@RequiredArgsConstructor
public class CourseContentController {

    private final CourseContentService courseContentService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("userId") Long userId) {

        CourseContentResponseDTO content = courseContentService.uploadFile(file, courseId, userId);

        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setSuccess(true);
        response.setMessage("File uploaded successfully");
        response.setData(content);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseContentResponseDTO> getContentById(@PathVariable Long id) {
        CourseContentResponseDTO content = courseContentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<CourseContentResponseDTO>> getContentsByCourseId(@PathVariable Long courseId) {
        List<CourseContentResponseDTO> contents = courseContentService.getContentsByCourseId(courseId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CourseContentResponseDTO>> getContentsByUserId(@PathVariable Long userId) {
        List<CourseContentResponseDTO> contents = courseContentService.getContentsByUserId(userId);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            HttpServletRequest request) {

        // Load file as Resource
        Resource resource = courseContentService.downloadFile(id);

        // Get content metadata
        CourseContentResponseDTO content = courseContentService.getContentById(id);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to default content type
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + content.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        courseContentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}

