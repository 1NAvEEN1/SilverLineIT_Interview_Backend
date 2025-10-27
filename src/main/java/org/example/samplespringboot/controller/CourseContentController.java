package org.example.samplespringboot.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.example.samplespringboot.dto.ApiResponse;
import org.example.samplespringboot.entity.CourseContent;
import org.example.samplespringboot.service.CourseContentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@RestController
@RequestMapping("/api/course-content")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseContentController {


    @Value("${document.file.dir}")
    String documentDir;

    private final CourseContentService courseContentService;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("fileName") String fileName,
                                                     @RequestParam("documentFile") MultipartFile submissions) {
        CourseContent courseContent = courseContentService.uploadDocument(submissions, fileName, documentDir);
        return ResponseEntity.ok(
                new ApiResponse<>("success","uploaded success", courseContent)
        );
    }

    @GetMapping("/download/{fileName}")
    public void viewDocument(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
        if (fileName != null && !("undefined".equalsIgnoreCase(fileName))) {
            File file = new File(documentDir + "/" + fileName);
            response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM.getType());
            response.setContentLength((int) file.length());
            InputStream is = new FileInputStream(file);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        }
    }
}

