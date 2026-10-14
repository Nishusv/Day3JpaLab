-- LAB 1 practice queries (work on PostgreSQL and MySQL). Run one at a time and read the result.

-- 1. Basic select / filter / sort
SELECT * FROM students WHERE age >= 20 ORDER BY age DESC;

-- 2. INNER JOIN (many-to-one): student with department name
SELECT s.name, d.name AS department
FROM students s
JOIN departments d ON d.id = s.department_id;

-- 3. LEFT JOIN (one-to-one): include students without a profile
SELECT s.name, p.phone, p.address
FROM students s
LEFT JOIN student_profiles p ON p.student_id = s.id;

-- 4. Many-to-many through the join table
SELECT s.name AS student, c.title AS course
FROM students s
JOIN student_course sc ON sc.student_id = s.id
JOIN courses c         ON c.id = sc.course_id
ORDER BY s.name, c.title;

-- 5. GROUP BY + HAVING: how many courses per student (only those with 2 or more)
SELECT s.name, COUNT(*) AS course_count
FROM students s
JOIN student_course sc ON sc.student_id = s.id
GROUP BY s.name
HAVING COUNT(*) >= 2;

-- 6. Students per department (including departments with zero students)
SELECT d.name, COUNT(s.id) AS students
FROM departments d
LEFT JOIN students s ON s.department_id = d.id
GROUP BY d.name;

-- 7. Subquery: students enrolled in the most popular course
SELECT name FROM students WHERE id IN (
    SELECT student_id FROM student_course WHERE course_id = (
        SELECT course_id FROM student_course GROUP BY course_id ORDER BY COUNT(*) DESC LIMIT 1
    )
);

-- 8. Constraint experiments (each one should FAIL - read the error message!)
-- INSERT INTO students (name, email, age) VALUES ('Dup', 'arjun@example.com', 20);   -- UNIQUE
-- INSERT INTO students (name, email, age) VALUES ('Old', 'old@example.com', 500);    -- CHECK
-- INSERT INTO students (name, email, age, department_id) VALUES ('X', 'x@example.com', 20, 99); -- FOREIGN KEY
-- DELETE FROM departments WHERE id = 1;                                               -- FK still referenced
