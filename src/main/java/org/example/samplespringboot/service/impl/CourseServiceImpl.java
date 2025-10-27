package org.example.samplespringboot.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.samplespringboot.dto.CourseRequestDTO;
import org.example.samplespringboot.dto.CourseResponseDTO;
import org.example.samplespringboot.dto.CourseContentResponseDTO;
import org.example.samplespringboot.entity.Course;
import org.example.samplespringboot.entity.User;
import org.example.samplespringboot.exception.DuplicateResourceException;
import org.example.samplespringboot.exception.ResourceNotFoundException;
import org.example.samplespringboot.repository.CourseRepository;
import org.example.samplespringboot.repository.UserRepository;
import org.example.samplespringboot.service.CourseService;
import org.example.samplespringboot.service.CourseContentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseContentService courseContentService;

    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO courseRequestDTO) {
        // Check if course code already exists
        if (courseRepository.findByCourseCode(courseRequestDTO.getCourseCode()).isPresent()) {
            throw new DuplicateResourceException("Course with code " + courseRequestDTO.getCourseCode() + " already exists");
        }

        // Verify instructor exists
        User instructor = userRepository.findById(courseRequestDTO.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + courseRequestDTO.getInstructorId()));

        Course course = new Course();
        course.setCourseName(courseRequestDTO.getCourseName());
        course.setCourseCode(courseRequestDTO.getCourseCode());
        course.setDescription(courseRequestDTO.getDescription());
        course.setInstructor(instructor);

        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO createCourseWithContent(CourseRequestDTO courseRequestDTO, List<MultipartFile> files) {
        // Create course first
        CourseResponseDTO courseResponse = createCourse(courseRequestDTO);

        // Upload files if provided
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    courseContentService.uploadFile(file, courseResponse.getId(), courseRequestDTO.getInstructorId());
                }
            }
        }

        // Return course with contents
        return getCourseByIdWithContents(courseResponse.getId());
    }

    @Override
    public CourseResponseDTO getCourseByIdWithContents(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        CourseResponseDTO dto = mapToDTO(course);

        // Fetch course contents (excluding soft-deleted ones)
        List<CourseContentResponseDTO> contents = courseContentService.getContentsByCourseId(id);
        dto.setContents(contents);

        return dto;
    }

    @Override
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    @Override
    public List<CourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDTO> getCoursesByInstructorId(Long instructorId) {
        List<Course> courses = courseRepository.findByInstructorId(instructorId);
        return courses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO courseRequestDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Check if course code is being changed and if it already exists
        if (!course.getCourseCode().equals(courseRequestDTO.getCourseCode())) {
            if (courseRepository.findByCourseCode(courseRequestDTO.getCourseCode()).isPresent()) {
                throw new DuplicateResourceException("Course with code " + courseRequestDTO.getCourseCode() + " already exists");
            }
        }

        // Verify instructor exists if changed
        if (!course.getInstructor().getId().equals(courseRequestDTO.getInstructorId())) {
            User instructor = userRepository.findById(courseRequestDTO.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + courseRequestDTO.getInstructorId()));
            course.setInstructor(instructor);
        }

        course.setCourseName(courseRequestDTO.getCourseName());
        course.setCourseCode(courseRequestDTO.getCourseCode());
        course.setDescription(courseRequestDTO.getDescription());

        Course updatedCourse = courseRepository.save(course);
        return mapToDTO(updatedCourse);
    }

    @Override
    @Transactional
    public CourseResponseDTO updateCourseWithContent(Long id, CourseRequestDTO courseRequestDTO, List<MultipartFile> files) {
        // Update course first
        updateCourse(id, courseRequestDTO);

        // Upload new files if provided
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    courseContentService.uploadFile(file, id, courseRequestDTO.getInstructorId());
                }
            }
        }

        // Return course with contents
        return getCourseByIdWithContents(id);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
    }

    private CourseResponseDTO mapToDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setCourseCode(course.getCourseCode());
        dto.setDescription(course.getDescription());
        dto.setInstructorId(course.getInstructor().getId());
        dto.setInstructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        return dto;
    }
}
