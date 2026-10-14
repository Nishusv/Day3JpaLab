package com.lab.jpa.repository;

import com.lab.jpa.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByTitleIgnoreCase(String title);
}
