package com.lab.jpa.controller;

import com.lab.jpa.dto.PageResponse;
import com.lab.jpa.dto.StudentRequest;
import com.lab.jpa.dto.StudentResponse;
import com.lab.jpa.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // ---------- CRUD ----------
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // Query methods: ?name=  |  ?department=  |  ?course=  |  ?minAge=
    @GetMapping
    public List<StudentResponse> search(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String department,
                                        @RequestParam(required = false) String course,
                                        @RequestParam(required = false) Integer minAge) {
        return service.search(name, department, course, minAge);
    }

    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Relationships ----------
    @PostMapping("/{studentId}/courses/{courseId}")
    public StudentResponse enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        return service.enroll(studentId, courseId);
    }

    // ---------- Extra query demos ----------
    @GetMapping("/page")
    public PageResponse<StudentResponse> page(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "asc") String direction) {
        return service.page(page, size, sortBy, direction);
    }

    @GetMapping("/join-fetch")
    public List<StudentResponse> joinFetch() {
        return service.findAllWithDepartment();
    }

    @GetMapping("/age-range")
    public List<StudentResponse> ageRange(@RequestParam int min, @RequestParam int max) {
        return service.findByAgeRange(min, max);
    }
}
