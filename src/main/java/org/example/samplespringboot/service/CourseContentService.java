package org.example.samplespringboot.service;
import org.example.samplespringboot.entity.CourseContent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseContentService {
    CourseContent uploadDocument(MultipartFile multipartFile,String fileName, String documentDir);
}

