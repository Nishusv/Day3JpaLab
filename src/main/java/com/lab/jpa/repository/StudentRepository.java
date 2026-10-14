package com.lab.jpa.repository;

import com.lab.jpa.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Inherited from JpaRepository (no code needed):
 * save, findById, findAll, findAll(Pageable), deleteById, count, existsById ...
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ---- 1. Derived query methods: Spring builds the query from the method name ----
    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByDepartmentNameIgnoreCase(String departmentName);

    List<Student> findByAgeGreaterThanEqualOrderByAgeDesc(int age);

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    // ---- 2. JPQL: query written against entities and fields (not tables/columns) ----
    @Query("select s from Student s join s.courses c where c.title = :title order by s.name")
    List<Student> findByCourseTitle(@Param("title") String title);

    // join fetch loads the department in the SAME select (avoids the N+1 problem)
    @Query("select s from Student s left join fetch s.department order by s.id")
    List<Student> findAllWithDepartment();

    // ---- 3. Native SQL: plain SQL against the real table ----
    @Query(value = "select * from students where age between :min and :max order by age, id",
            nativeQuery = true)
    List<Student> findByAgeRangeNative(@Param("min") int min, @Param("max") int max);
}
