package com.lab.jpa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    private Integer age;

    // Many students -> one department  (owning side, holds the department_id foreign key)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // One student -> one profile  (StudentProfile owns the foreign key)
    // cascade ALL: saving/deleting a student also saves/deletes the profile
    // orphanRemoval: a profile removed from the student is deleted from the table
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private StudentProfile profile;

    // Many students <-> many courses  (owning side, creates the student_course join table)
    @ManyToMany
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    public Student() {
    }

    /** Keeps both sides of the relationship in sync. */
    public void setProfile(StudentProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setStudent(this);
        }
    }

    /** Keeps both sides of the many-to-many in sync. */
    public void enroll(Course course) {
        this.courses.add(course);
        course.getStudents().add(this);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public StudentProfile getProfile() { return profile; }
    public Set<Course> getCourses() { return courses; }
}
