package com.lab.jpa.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Plain JDBC - no Spring, no Hibernate. Run this class's main() directly.
 *
 * Default database: in-memory H2 (zero setup).
 * To run against PostgreSQL / MySQL pass Program arguments:  <url> <user> <password>
 *   jdbc:postgresql://localhost:5432/studentdb postgres your_password
 *   jdbc:mysql://localhost:3306/studentdb root your_password
 * (the database must already exist; the demo creates its own table jdbc_students)
 */
public class JdbcDemo {

    public static void main(String[] args) throws SQLException {
        String url = args.length > 0 ? args[0] : "jdbc:h2:mem:jdbcdemo";
        String user = args.length > 1 ? args[1] : "sa";
        String password = args.length > 2 ? args[2] : "";

        // STEP 1 - open a connection (try-with-resources closes it automatically)
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to: " + con.getMetaData().getDatabaseProductName());

            createTable(con);
            insertStudents(con);
            printAll(con, "After INSERT");
            injectionDemo(con);
            transfer(con, 1, 2, 10, true);   // fails half way -> rollback
            printAll(con, "After FAILED transfer (rolled back)");
            transfer(con, 1, 2, 10, false);  // succeeds -> commit
            printAll(con, "After SUCCESSFUL transfer (committed)");
        }
    }

    // STEP 2 - Statement: run fixed SQL with no parameters
    private static void createTable(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("DROP TABLE IF EXISTS jdbc_students");
            st.execute("CREATE TABLE jdbc_students ("
                    + "id INT PRIMARY KEY, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "age INT, "
                    + "marks INT)");
        }
        System.out.println("Table jdbc_students created");
    }

    // STEP 3 - PreparedStatement: SQL with ? placeholders, values set separately (+ batching)
    private static void insertStudents(Connection con) throws SQLException {
        String sql = "INSERT INTO jdbc_students (id, name, age, marks) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            Object[][] rows = {
                    {1, "Arjun Rao", 20, 80},
                    {2, "Meera Nair", 21, 90},
                    {3, "Rahul Verma", 19, 70}
            };
            for (Object[] row : rows) {
                ps.setInt(1, (Integer) row[0]);
                ps.setString(2, (String) row[1]);
                ps.setInt(3, (Integer) row[2]);
                ps.setInt(4, (Integer) row[3]);
                ps.addBatch();
            }
            int[] counts = ps.executeBatch();
            System.out.println("Inserted rows: " + counts.length);
        }
    }

    // STEP 4 - ResultSet: walk through the rows returned by a SELECT
    private static void printAll(Connection con, String title) throws SQLException {
        System.out.println("\n--- " + title + " ---");
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, age, marks FROM jdbc_students ORDER BY id")) {
            while (rs.next()) {
                System.out.printf("id=%d  name=%-12s age=%d  marks=%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("age"), rs.getInt("marks"));
            }
        }
    }

    // STEP 5 - why PreparedStatement matters: SQL injection
    private static void injectionDemo(Connection con) throws SQLException {
        String maliciousInput = "x' OR '1'='1";

        String unsafeSql = "SELECT * FROM jdbc_students WHERE name = '" + maliciousInput + "'";
        int unsafeCount = 0;
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(unsafeSql)) {
            while (rs.next()) {
                unsafeCount++;
            }
        }

        int safeCount = 0;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM jdbc_students WHERE name = ?")) {
            ps.setString(1, maliciousInput);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    safeCount++;
                }
            }
        }
        System.out.println("\nSQL injection demo");
        System.out.println("  String concatenation returned " + unsafeCount + " rows (attack worked!)");
        System.out.println("  PreparedStatement returned   " + safeCount + " rows (attack blocked)");
    }

    // STEP 6 - transaction: move marks from one student to another, all or nothing
    private static void transfer(Connection con, int fromId, int toId, int amount, boolean failHalfway)
            throws SQLException {
        System.out.println("\nTransfer " + amount + " marks from " + fromId + " to " + toId
                + (failHalfway ? "  (simulating a crash)" : ""));
        con.setAutoCommit(false); // start a transaction
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE jdbc_students SET marks = marks + ? WHERE id = ?")) {
            ps.setInt(1, -amount);
            ps.setInt(2, fromId);
            ps.executeUpdate();

            if (failHalfway) {
                throw new SQLException("Simulated failure after debit, before credit");
            }

            ps.setInt(1, amount);
            ps.setInt(2, toId);
            ps.executeUpdate();

            con.commit();
            System.out.println("  COMMIT");
        } catch (SQLException e) {
            con.rollback();
            System.out.println("  ROLLBACK because: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }
}
