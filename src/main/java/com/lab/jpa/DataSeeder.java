package com.lab.jpa;

import com.lab.jpa.dto.StudentRequest;
import com.lab.jpa.dto.StudentResponse;
import com.lab.jpa.model.Course;
import com.lab.jpa.model.Department;
import com.lab.jpa.repository.CourseRepository;
import com.lab.jpa.repository.DepartmentRepository;
import com.lab.jpa.service.StudentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Inserts sample data on startup, only when the database is empty. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentService studentService;

    public DataSeeder(DepartmentRepository departmentRepository,
                      CourseRepository courseRepository,
                      StudentService studentService) {
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.studentService = studentService;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        Department cs = departmentRepository.save(new Department("Computer Science"));
        Department ec = departmentRepository.save(new Department("Electronics"));

        Course java = courseRepository.save(new Course("Java Programming", 4));
        Course dsa = courseRepository.save(new Course("Data Structures", 4));
        Course dbms = courseRepository.save(new Course("Database Systems", 3));
        courseRepository.save(new Course("Web Development", 3));

        StudentResponse arjun = studentService.create(
                new StudentRequest("Arjun Rao", "arjun@example.com", 20, cs.getId(), "9876543210", "Bengaluru"));
        StudentResponse meera = studentService.create(
                new StudentRequest("Meera Nair", "meera@example.com", 21, cs.getId(), "9123456780", "Mysuru"));
        StudentResponse rahul = studentService.create(
                new StudentRequest("Rahul Verma", "rahul@example.com", 19, ec.getId(), null, null));

        studentService.enroll(arjun.id(), java.getId());
        studentService.enroll(arjun.id(), dsa.getId());
        studentService.enroll(meera.id(), java.getId());
        studentService.enroll(meera.id(), dbms.getId());
        studentService.enroll(rahul.id(), dbms.getId());
    }
}
