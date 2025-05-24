package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner queryrunner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("db.url"), "app", "pass");
    }

    @SneakyThrows
    public static String getLatestPaymentStatus() {
        var codeSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            return queryrunner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static String getLatestCreditStatus() {
        var codeSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            return queryrunner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConnection()) {
            queryrunner.execute(conn, "DELETE FROM payment_entity");
            queryrunner.execute(conn, "DELETE FROM credit_request_entity");
            queryrunner.execute(conn, "DELETE FROM order_entity");
        }
    }
}