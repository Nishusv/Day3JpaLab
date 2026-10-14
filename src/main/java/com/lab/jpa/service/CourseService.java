package com.lab.jpa.service;

import com.lab.jpa.dto.CourseRequest;
import com.lab.jpa.dto.CourseResponse;
import com.lab.jpa.exception.BusinessRuleException;
import com.lab.jpa.model.Course;
import com.lab.jpa.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public CourseResponse create(CourseRequest request) {
        if (courseRepository.findByTitleIgnoreCase(request.title()).isPresent()) {
            throw new BusinessRuleException("Course already exists: " + request.title());
        }
        Course saved = courseRepository.save(new Course(request.title(), request.credits()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findAll() {
        return courseRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CourseResponse toResponse(Course c) {
        return new CourseResponse(c.getId(), c.getTitle(), c.getCredits(), c.getStudents().size());
    }
}
