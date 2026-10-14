package com.lab.jpa.service;

import com.lab.jpa.dto.StudentRequest;
import com.lab.jpa.dto.StudentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction management demo.
 * Both methods do exactly the same work. The ONLY difference is @Transactional.
 * Send a list where the 2nd student breaks a rule (e.g. duplicate email):
 *   - without a transaction the 1st student stays saved (partial data!)
 *   - with one transaction everything is rolled back (all or nothing)
 */
@Service
public class BulkStudentService {

    private final StudentService studentService;

    public BulkStudentService(StudentService studentService) {
        this.studentService = studentService;
    }

    // No outer transaction: each studentService.create() commits on its own.
    public List<StudentResponse> saveAllWithoutTransaction(List<StudentRequest> requests) {
        List<StudentResponse> saved = new ArrayList<>();
        for (StudentRequest request : requests) {
            saved.add(studentService.create(request));
        }
        return saved;
    }

    // One transaction around the whole loop: any exception rolls back every insert.
    @Transactional
    public List<StudentResponse> saveAllInOneTransaction(List<StudentRequest> requests) {
        List<StudentResponse> saved = new ArrayList<>();
        for (StudentRequest request : requests) {
            saved.add(studentService.create(request));
        }
        return saved;
    }
}
