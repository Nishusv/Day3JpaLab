package com.lab.jpa.service;

import com.lab.jpa.dto.DepartmentRequest;
import com.lab.jpa.dto.DepartmentResponse;
import com.lab.jpa.exception.BusinessRuleException;
import com.lab.jpa.exception.ResourceNotFoundException;
import com.lab.jpa.model.Department;
import com.lab.jpa.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new BusinessRuleException("Department already exists: " + request.name());
        }
        Department saved = departmentRepository.save(new Department(request.name()));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void delete(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        if (!department.getStudents().isEmpty()) {
            throw new BusinessRuleException("Cannot delete a department that still has students");
        }
        departmentRepository.delete(department);
    }

    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getStudents().size());
    }
}
