-- LAB 1 (MySQL / Workbench or CLI)
-- Uses a SEPARATE practice database called designlab.

CREATE DATABASE IF NOT EXISTS designlab;
USE designlab;

DROP TABLE IF EXISTS student_course;
DROP TABLE IF EXISTS student_profiles;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS departments;

CREATE TABLE departments (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE students (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    age           INT,
    department_id INT,
    CONSTRAINT chk_age CHECK (age BETWEEN 3 AND 100),
    CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB;

-- One-to-one: UNIQUE foreign key
CREATE TABLE student_profiles (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL UNIQUE,
    phone      VARCHAR(20),
    address    VARCHAR(255),
    CONSTRAINT fk_profile_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE courses (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    title   VARCHAR(100) NOT NULL UNIQUE,
    credits INT NOT NULL,
    CONSTRAINT chk_credits CHECK (credits BETWEEN 1 AND 10)
) ENGINE=InnoDB;

-- Many-to-many: join table with a composite primary key
CREATE TABLE student_course (
    student_id INT NOT NULL,
    course_id  INT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_sc_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_sc_course  FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO departments (name) VALUES ('Computer Science'), ('Electronics');
INSERT INTO courses (title, credits) VALUES
    ('Java Programming', 4), ('Data Structures', 4), ('Database Systems', 3), ('Web Development', 3);
INSERT INTO students (name, email, age, department_id) VALUES
    ('Arjun Rao',   'arjun@example.com', 20, 1),
    ('Meera Nair',  'meera@example.com', 21, 1),
    ('Rahul Verma', 'rahul@example.com', 19, 2);
INSERT INTO student_profiles (student_id, phone, address) VALUES
    (1, '9876543210', 'Bengaluru'), (2, '9123456780', 'Mysuru');
INSERT INTO student_course (student_id, course_id) VALUES
    (1, 1), (1, 2), (2, 1), (2, 3), (3, 3);
