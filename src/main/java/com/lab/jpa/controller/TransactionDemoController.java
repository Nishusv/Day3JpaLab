package com.lab.jpa.controller;

import com.lab.jpa.dto.StudentRequest;
import com.lab.jpa.dto.StudentResponse;
import com.lab.jpa.service.BulkStudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tx-demo")
public class TransactionDemoController {

    private final BulkStudentService bulkService;

    public TransactionDemoController(BulkStudentService bulkService) {
        this.bulkService = bulkService;
    }

    // POST /api/tx-demo/students?transactional=true|false   body: [ {student}, {student}, ... ]
    @PostMapping("/students")
    public ResponseEntity<List<StudentResponse>> saveMany(
            @RequestParam(defaultValue = "true") boolean transactional,
            @RequestBody List<StudentRequest> requests) {
        List<StudentResponse> saved = transactional
                ? bulkService.saveAllInOneTransaction(requests)
                : bulkService.saveAllWithoutTransaction(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
