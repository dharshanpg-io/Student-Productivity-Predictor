package com.studentproductivity.database;

import com.studentproductivity.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages JDBC connection lifecycle, connection testing, and automatic schema initialization for MySQL 8.
 */
public class DatabaseManager {

    private final AppConfig config;
    private Boolean isAvailable = null;
    private String lastError = null;

    public DatabaseManager() {
        this.config = AppConfig.getInstance();
    }

    /**
     * Attempts to acquire a database connection.
     */
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(config.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver class not found: " + config.getDbDriver(), e);
        }

        DriverManager.setLoginTimeout(Math.max(1, config.getDbConnectTimeout() / 1000));
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
    }

    /**
     * Checks if MySQL database is reachable without throwing unhandled exceptions.
     */
    public synchronized boolean isDatabaseReachable() {
        try (Connection conn = getConnection()) {
            isAvailable = (conn != null && !conn.isClosed());
            lastError = null;
            if (isAvailable && config.isDbAutoCreate()) {
                initializeSchema(conn);
            }
            return isAvailable;
        } catch (Exception e) {
            isAvailable = false;
            lastError = e.getMessage();
            return false;
        }
    }

    /**
     * Initializes the database table if it doesn't already exist.
     */
    public void initializeSchema(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS prediction_history (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "age INT NOT NULL," +
                "gender VARCHAR(20) NOT NULL," +
                "study_hours_per_day DOUBLE NOT NULL," +
                "sleep_hours DOUBLE NOT NULL," +
                "phone_usage_hours DOUBLE NOT NULL," +
                "social_media_hours DOUBLE NOT NULL," +
                "youtube_hours DOUBLE NOT NULL," +
                "gaming_hours DOUBLE NOT NULL," +
                "breaks_per_day INT NOT NULL," +
                "coffee_intake_mg DOUBLE NOT NULL," +
                "exercise_minutes DOUBLE NOT NULL," +
                "assignments_completed INT NOT NULL," +
                "attendance_percentage DOUBLE NOT NULL," +
                "stress_level INT NOT NULL," +
                "focus_score DOUBLE NOT NULL," +
                "final_grade DOUBLE NOT NULL," +
                "predicted_score DOUBLE NOT NULL," +
                "model_version VARCHAR(50) DEFAULT 'RandomForest-v1.0'," +
                "input_data TEXT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Notice: Could not automatically initialize schema: " + e.getMessage());
        }
    }

    public String getLastError() {
        return lastError;
    }

    public Boolean getAvailabilityStatus() {
        return isAvailable;
    }
}
