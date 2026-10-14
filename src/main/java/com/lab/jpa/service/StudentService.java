package com.lab.jpa.service;

import com.lab.jpa.dto.PageResponse;
import com.lab.jpa.dto.StudentRequest;
import com.lab.jpa.dto.StudentResponse;
import com.lab.jpa.exception.BusinessRuleException;
import com.lab.jpa.exception.ResourceNotFoundException;
import com.lab.jpa.model.Course;
import com.lab.jpa.model.Student;
import com.lab.jpa.model.StudentProfile;
import com.lab.jpa.repository.CourseRepository;
import com.lab.jpa.repository.DepartmentRepository;
import com.lab.jpa.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Transactional = one database transaction per method call.
 * Everything inside either commits together or rolls back together.
 * Reading lazy fields (department, courses) is only allowed INSIDE the transaction,
 * which is why entities are converted to DTOs here and not in the controller.
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
    }

    // ------------------------- CREATE -------------------------
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Email already registered: " + request.email());
        }
        Student student = new Student();
        applyRequest(student, request);
        return toResponse(studentRepository.save(student));
    }

    // ------------------------- READ -------------------------
    @Transactional(readOnly = true)
    public List<StudentResponse> search(String name, String department, String course, Integer minAge) {
        List<Student> students;
        if (name != null && !name.isBlank()) {
            students = studentRepository.findByNameContainingIgnoreCase(name);
        } else if (department != null && !department.isBlank()) {
            students = studentRepository.findByDepartmentNameIgnoreCase(department);
        } else if (course != null && !course.isBlank()) {
            students = studentRepository.findByCourseTitle(course);
        } else if (minAge != null) {
            students = studentRepository.findByAgeGreaterThanEqualOrderByAgeDesc(minAge);
        } else {
            students = studentRepository.findAll();
        }
        return students.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAllWithDepartment() {
        return studentRepository.findAllWithDepartment().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findByAgeRange(int min, int max) {
        return studentRepository.findByAgeRangeNative(min, max).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> page(int page, int size, String sortBy, String direction) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Page<Student> result = studentRepository.findAll(PageRequest.of(page, size, Sort.by(dir, sortBy)));
        List<StudentResponse> content = result.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(content, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return toResponse(getStudent(id));
    }

    // ------------------------- UPDATE -------------------------
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getStudent(id);
        studentRepository.findByEmail(request.email()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new BusinessRuleException("Email already registered: " + request.email());
            }
        });
        applyRequest(student, request);
        // No save() call needed: Hibernate "dirty checking" notices the changes
        // on the managed entity and issues the UPDATE when the transaction commits.
        return toResponse(student);
    }

    // ------------------------- DELETE -------------------------
    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getStudent(id));
    }

    // ------------------------- ENROLL (many-to-many) -------------------------
    @Transactional
    public StudentResponse enroll(Long studentId, Long courseId) {
        Student student = getStudent(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        if (student.getCourses().contains(course)) {
            throw new BusinessRuleException(student.getName() + " is already enrolled in " + course.getTitle());
        }
        student.enroll(course);
        return toResponse(student);
    }

    // ------------------------- helpers -------------------------
    private Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private void applyRequest(Student student, StudentRequest request) {
        student.setName(request.name());
        student.setEmail(request.email());
        student.setAge(request.age());

        if (request.departmentId() != null) {
            student.setDepartment(departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id: " + request.departmentId())));
        } else {
            student.setDepartment(null);
        }

        if (request.phone() != null || request.address() != null) {
            StudentProfile profile = student.getProfile() != null ? student.getProfile() : new StudentProfile();
            profile.setPhone(request.phone());
            profile.setAddress(request.address());
            student.setProfile(profile);
        }
    }

    private StudentResponse toResponse(Student s) {
        String departmentName = s.getDepartment() != null ? s.getDepartment().getName() : null;
        String phone = s.getProfile() != null ? s.getProfile().getPhone() : null;
        String address = s.getProfile() != null ? s.getProfile().getAddress() : null;
        List<String> courseTitles = s.getCourses().stream().map(Course::getTitle).sorted().toList();
        return new StudentResponse(s.getId(), s.getName(), s.getEmail(), s.getAge(),
                departmentName, phone, address, courseTitles);
    }
}
