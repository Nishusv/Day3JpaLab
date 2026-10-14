# Day 3 JPA Lab: Spring Data JPA + Hibernate + JDBC

Hands-on lab for Day 3 (Database Integration with Spring Data JPA).
Full instructions: **docs/LAB_GUIDE.md**

## Quick start
1. IntelliJ: File > Open > this folder (has pom.xml) > wait for Maven.
2. Run `Day3JpaLabApplication` (H2 in-memory, no setup).
3. Import `postman/Day3JpaLab.postman_collection.json` into Postman.

## Databases
- Default: H2 in-memory (console at http://localhost:8080/h2-console)
- PostgreSQL: create `studentdb`, set password in `application-postgres.properties`, run with active profile `postgres`
- MySQL: set password in `application-mysql.properties`, run with active profile `mysql`

## Other pieces
- `src/main/java/com/lab/jpa/jdbc/JdbcDemo.java`: plain JDBC demo (run its main)
- `sql/`: hand-written schema and practice queries for pgAdmin / MySQL


## 🚀 Key Features

- **Database Design:** Tables, keys, constraints, joins, and aggregate queries.
- **JDBC Basics:** Connections, statements, prepared statements, and result sets.
- **Spring Data JPA:** Repository methods, JPQL, native queries, and pagination.
- **Hibernate ORM:** Entity relationships, lazy/eager loading, cascading, and the N+1 problem.
- **CRUD & Transactions:** Dirty checking, commits, rollbacks, and `@Transactional`.
- **Database Switching:** Support for H2, PostgreSQL, and MySQL.

## 🛠️ Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate ORM
- JDBC
- Maven
- H2, PostgreSQL, MySQL
- Postman and pgAdmin

## ⚙️ Getting Started

1. Clone or download the repository.
2. Open the `Day3JpaLab` folder in IntelliJ IDEA as a Maven project.
3. Run `Day3JpaLabApplication`.
4. Import `postman/Day3JpaLab.postman_collection.json` into Postman.
5. Follow `docs/LAB_GUIDE.md` for step-by-step exercises.

The application starts with H2 by default. PostgreSQL and MySQL can be enabled using their respective Spring profiles.

## 🎯 Learning Outcomes

Learn how Java applications interact with databases, map entity relationships, execute queries, prevent SQL injection using prepared statements, and manage transactions.

**Note:** This project is intended for educational purposes. Verify the application locally before using it in production.
