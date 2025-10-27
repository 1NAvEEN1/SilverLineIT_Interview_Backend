package org.example.samplespringboot.repository;

import org.example.samplespringboot.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
    List<CourseContent> findByCourseId(Long courseId);
    List<CourseContent> findByUploadedById(Long userId);
    List<CourseContent> findByCourseIdAndIsDeletedFalse(Long courseId);
    List<CourseContent> findByUploadedByIdAndIsDeletedFalse(Long userId);
}

