# Day 3 Hands-on Lab: Database Integration with Spring Data JPA

**Tools:** IntelliJ IDEA, Postman, pgAdmin, PostgreSQL, MySQL, Spring Data JPA, Hibernate
**Format:** 6 labs, about 4.5 hours of hands-on work. Each lab = what to do, what you should see, your turn, check questions.

---

## 0. Setup (15 min)

1. Install JDK 17+, IntelliJ IDEA, Postman, PostgreSQL + pgAdmin (and MySQL if you want Lab 6b).
2. Unzip, then in IntelliJ: File > Open > select the `Day3JpaLab` folder (contains `pom.xml`) > Trust Project. Wait for Maven to finish.
3. Run `Day3JpaLabApplication`. Wait for `Started Day3JpaLabApplication`.
4. Open `http://localhost:8080/api/students` in a browser. You should see 3 students as JSON.
5. Postman: Import `postman/Day3JpaLab.postman_collection.json`.

The app starts on **H2 (in-memory)** so everything works with no database install. In Lab 6 you switch to PostgreSQL and MySQL by changing one setting.

### Project map

| Package / file | Concept |
|---|---|
| `sql/` | Lab 1: schema design scripts for PostgreSQL and MySQL |
| `jdbc/JdbcDemo.java` | Lab 2: plain JDBC |
| `model/` | Labs 3-4: Hibernate entities and relationships |
| `repository/` | Lab 3: Spring Data JPA repositories and query methods |
| `service/` | Labs 4-5: CRUD logic and `@Transactional` |
| `controller/` | REST endpoints you call from Postman |
| `postman/` | Ready-made requests with tests |

---

## Lab 1: Relational design (45 min)

**Goal:** design tables, keys and relationships by hand before any Java.

**ER summary**
- Department 1 --- N Student (foreign key `students.department_id`)
- Student 1 --- 1 StudentProfile (unique foreign key `student_profiles.student_id`)
- Student N --- M Course (join table `student_course`, composite primary key)

**Steps**
1. In pgAdmin create a database named `designlab` (right-click Databases > Create > Database).
2. Open Tools > Query Tool on `designlab`, paste `sql/01_schema_postgres.sql`, and run it (F5).
3. Expand designlab > Schemas > public > Tables and confirm the 5 tables exist. Right-click `students` > View/Edit Data.
4. Run the queries in `sql/02_practice_queries.sql` one at a time. Read each result.
5. MySQL users: run `sql/01_schema_mysql.sql` in Workbench or the `mysql` CLI instead.

**Your turn**
- Run the four failing experiments at the bottom of the practice file. Which constraint fires for each?
- Add a `grade` column to `students` using `ALTER TABLE`.
- Design (on paper or SQL) a `teachers` table so one teacher teaches many courses. Where does the foreign key go and why?
- Explain why `student_course` needs its own table instead of a `course_id` column in `students`.

**Check questions:** primary key vs foreign key? What does normalization prevent? What does `ON DELETE CASCADE` do?

---

## Lab 2: JDBC basics (45 min)

**Goal:** see what Hibernate hides: connections, statements, result sets.

**Steps**
1. Open `jdbc/JdbcDemo.java` and run its `main` (right-click > Run). It uses in-memory H2.
2. Read the console output and match each printed section to the numbered `STEP` comments in the code.

**Expected output (shape):** `Connected to: H2`, 3 inserted rows, the injection demo (`String concatenation returned 3 rows`, `PreparedStatement returned 0 rows`), a failed transfer that rolls back (marks unchanged), then a successful one (80 becomes 70, 90 becomes 100).

**Run it on PostgreSQL:** Run > Edit Configurations > select `JdbcDemo` > Program arguments:
```
jdbc:postgresql://localhost:5432/designlab postgres your_password
```
Run again, then check the `jdbc_students` table in pgAdmin. For MySQL use `jdbc:mysql://localhost:3306/designlab root your_password`.

**Your turn**
- Add a `deleteById(Connection con, int id)` method using a `PreparedStatement`.
- Add a method that prints column names using `ResultSetMetaData`.
- Remove the `con.rollback()` call and see what happens to the failed transfer.
- Change `setAutoCommit(false)` handling and explain why `finally` resets it.

**Check questions:** Statement vs PreparedStatement? Why try-with-resources? What are commit and rollback? (Spring Boot uses a connection pool, HikariCP, so connections are reused.)

---

## Lab 3: Spring Data JPA (60 min)

**Goal:** go from JDBC boilerplate to repositories with zero SQL.

**Steps**
1. Open `repository/StudentRepository.java`. It has no implementation class. Spring generates it at startup.
2. Look at the startup console: Hibernate prints the `create table ...` statements it generated from the entities.
3. Open `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:studentdb`, user `sa`, empty password) and browse the tables.
4. In Postman open the folder **Lab 3** and send each request. After each one, look at the SQL printed in the IntelliJ console.

| Request | Repository method | Style |
|---|---|---|
| name contains | `findByNameContainingIgnoreCase` | derived query |
| by department | `findByDepartmentNameIgnoreCase` | derived (follows a relationship) |
| age >= | `findByAgeGreaterThanEqualOrderByAgeDesc` | derived with ordering |
| students in a course | `findByCourseTitle` | JPQL `@Query` |
| age between | `findByAgeRangeNative` | native SQL |
| paging & sorting | `findAll(Pageable)` | built in |

**Your turn**
- Add `List<Student> findByEmailEndingWith(String suffix)` and expose it in the service and controller.
- Add `long countByDepartmentNameIgnoreCase(String name)`.
- Write a JPQL query for students whose name starts with a given letter.
- Add an `Optional<Student>` lookup by email and return 404 if missing.
- Sort the page endpoint by `age` descending. What happens with `sortBy=salary`? (Read the error response.)

**Check questions:** What does `JpaRepository<Student, Long>` mean? When would you choose derived vs JPQL vs native? What does `ddl-auto=update` do and why is it risky in production?

---

## Lab 4: Hibernate mappings and relationships (45 min)

**Goal:** understand how annotations become tables, columns and join tables.

Read `model/Student.java`, `Department.java`, `Course.java`, `StudentProfile.java`.

| Annotation | Effect |
|---|---|
| `@Entity`, `@Table` | class maps to a table |
| `@Id`, `@GeneratedValue` | primary key, auto-increment |
| `@Column` | column rules (nullable, unique, length) |
| `@ManyToOne` + `@JoinColumn` | foreign key column |
| `@OneToMany(mappedBy=...)` | inverse side, no extra column |
| `@OneToOne` | one-to-one (unique foreign key) |
| `@ManyToMany` + `@JoinTable` | creates `student_course` |
| `cascade`, `orphanRemoval` | save/delete children with the parent |
| `fetch = LAZY` | load related data only when accessed |

**Steps** (Postman folder **Lab 4**, run requests 1 to 9 in order)
1. Create a department, course, and student with a profile.
2. Enroll the student, then look at the `student_course` table in the H2 console. A new row appeared even though you never wrote SQL.
3. Enroll again to trigger the 409 business rule.
4. **N+1 problem:** send request 8, then count the `select` statements in the console (one for students plus extra ones for departments and courses). Now send request 9 (`join fetch`) and compare. The department is now loaded in the same query.

**Your turn**
- Change `@ManyToOne(fetch = LAZY)` to `EAGER` on `Student.department`, restart, and compare the SQL.
- Delete the student (Lab 5) and confirm the profile row and join-table rows are gone. Which annotations caused that?
- Remove `orphanRemoval = true`, set a student's profile to null, and observe.
- Why do the controllers return DTOs and never entities? (Try returning an entity: bidirectional relationships cause infinite JSON recursion, and lazy fields fail outside a transaction. `spring.jpa.open-in-view=false` makes this visible.)

**Check questions:** What is the owning side of a relationship? What does `mappedBy` mean? Lazy vs eager?

---

## Lab 5: CRUD operations and transactions (45 min)

**Goal:** see exactly what a transaction protects you from.

| HTTP | Service method | SQL |
|---|---|---|
| POST /api/students | `create` | INSERT |
| GET /api/students/{id} | `findById` | SELECT |
| PUT /api/students/{id} | `update` | UPDATE (no `save()` call!) |
| DELETE /api/students/{id} | `delete` | DELETE (plus cascades) |

**Dirty checking:** in `update()` and `enroll()` there is no `save()`. The entity is *managed* inside the transaction, so Hibernate notices the change and issues the UPDATE or INSERT at commit.

**Transaction experiment** (Postman folder **Lab 5**)
1. Run **TX demo A** (`transactional=false`). The list has a good student then a duplicate email. It returns 409. Run the check request: *Partial Student exists*. Half the data was saved.
2. Run **TX demo B** (`transactional=true`) with the same shape. It also returns 409, but the check request shows *Rollback Student does not exist*. All or nothing.
3. Open `service/BulkStudentService.java`. The two methods are identical except for `@Transactional`.

**Your turn**
- Remove `@Transactional` from `saveAllInOneTransaction` and repeat demo B. What changes?
- Throw a *checked* exception (`throw new Exception(...)`) inside a transactional method. Does it roll back? (By default only runtime exceptions do. Fix it with `rollbackFor = Exception.class`.)
- Explain `@Transactional(readOnly = true)` and why the read methods use it.
- Add `@Transactional` to the controller instead. Why is the service layer the better place?

**Check questions:** What do ACID and propagation `REQUIRED` mean? What is dirty checking? What happens to the persistence context when the transaction ends?

---

## Lab 6: Switch databases with pgAdmin and MySQL (30 min)

**6a. PostgreSQL**
1. In pgAdmin create database `studentdb`.
2. Set your password in `src/main/resources/application-postgres.properties`.
3. Run > Edit Configurations > `Day3JpaLabApplication` > Active profiles: `postgres` (enable it under Modify options if hidden).
4. Run. In pgAdmin refresh `studentdb` > Schemas > public > Tables. Hibernate created `departments`, `students`, `courses`, `student_profiles`, `student_course`.
5. Run the Postman collection again, then query `SELECT * FROM students;` in pgAdmin. Restart the app. Your data is still there (the seeder only inserts when the database is empty).

**6b. MySQL**
1. Set your password in `application-mysql.properties`.
2. Active profiles: `mysql` and run. The database is created automatically.
3. Verify the tables in Workbench or `mysql -u root -p` then `USE studentdb; SHOW TABLES;`.

**Your turn**
- Compare the `students` table DDL on H2, PostgreSQL and MySQL. What differs (auto-increment style, types)? Hibernate handled it for you.
- Try `spring.jpa.hibernate.ddl-auto` values `create`, `create-drop`, `validate`, `none` and explain each.
- Take the hand-written schema from Lab 1 and point Spring at it with `ddl-auto=validate`. What mismatches does Hibernate report?

---

## Take-home challenge

Add a **Teacher** entity: one teacher teaches many courses (`@OneToMany` / `@ManyToOne`). Add endpoints to create teachers, assign a course to a teacher, and list courses by teacher. Add a Postman request for each, and make assigning a course transactional.

## Troubleshooting

| Problem | Fix |
|---|---|
| Port 8080 in use | change `server.port` or stop the other app |
| `password authentication failed` | wrong password in the profile file |
| `Connection refused` | database service not running or wrong port |
| `database "studentdb" does not exist` | create it in pgAdmin first (PostgreSQL only) |
| `LazyInitializationException` | a lazy field was read outside a transaction; map to a DTO inside the service |
| Red errors after opening | reload Maven, set Project SDK to 17+ |
| Duplicate email 409 on re-run | expected on persistent databases; Postman uses `{{$timestamp}}` to avoid it |

## Outcomes checklist

- [ ] I can design tables with primary keys, foreign keys and join tables
- [ ] I can write JDBC code with `PreparedStatement` and handle commit and rollback
- [ ] I can write Spring Data JPA derived, JPQL and native queries
- [ ] I can map one-to-one, one-to-many and many-to-many with Hibernate
- [ ] I can explain lazy loading, the N+1 problem, cascade and orphan removal
- [ ] I can demonstrate the difference between running with and without a transaction
- [ ] I can run the same application on H2, PostgreSQL and MySQL
