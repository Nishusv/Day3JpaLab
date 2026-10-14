-- LAB 1 (PostgreSQL / pgAdmin)
-- First create a SEPARATE practice database (right-click Databases > Create > Database "designlab"),
-- then open Query Tool on designlab and run this script.

DROP TABLE IF EXISTS student_course, student_profiles, students, courses, departments CASCADE;

CREATE TABLE departments (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE students (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    age           INT CHECK (age BETWEEN 3 AND 100),
    department_id INT REFERENCES departments(id)
);

-- One-to-one: UNIQUE foreign key
CREATE TABLE student_profiles (
    id         SERIAL PRIMARY KEY,
    student_id INT NOT NULL UNIQUE REFERENCES students(id) ON DELETE CASCADE,
    phone      VARCHAR(20),
    address    VARCHAR(255)
);

CREATE TABLE courses (
    id      SERIAL PRIMARY KEY,
    title   VARCHAR(100) NOT NULL UNIQUE,
    credits INT NOT NULL CHECK (credits BETWEEN 1 AND 10)
);

-- Many-to-many: join table with a composite primary key
CREATE TABLE student_course (
    student_id INT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id  INT NOT NULL REFERENCES courses(id)  ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);

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
