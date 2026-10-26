package com.studentproductivity.database;

import com.google.gson.Gson;
import com.studentproductivity.prediction.PredictionRequest;
import com.studentproductivity.prediction.PredictionResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for persisting and querying student productivity prediction history.
 * Operates over MySQL 8 with automatic fallback to LocalHistoryStore when MySQL is unavailable.
 */
public class PredictionHistoryDao {

    private final DatabaseManager dbManager;
    private final LocalHistoryStore localStore;
    private final Gson gson = new Gson();

    public PredictionHistoryDao(DatabaseManager dbManager, LocalHistoryStore localStore) {
        this.dbManager = dbManager;
        this.localStore = localStore;
    }

    /**
     * Save prediction result to database or local storage.
     */
    public boolean savePrediction(PredictionResult result) {
        if (result == null) return false;
        PredictionRequest req = result.getRequest();
        String jsonInput = gson.toJson(req);

        HistoryRecord record = new HistoryRecord(
                0,
                new Timestamp(result.getTimestamp()),
                req.getAge(),
                req.getGender(),
                req.getStudyHoursPerDay(),
                req.getSleepHours(),
                req.getPhoneUsageHours(),
                req.getAttendancePercentage(),
                req.getStressLevel(),
                req.getFocusScore(),
                result.getPredictedScore(),
                result.getModelVersion(),
                jsonInput
        );

        if (dbManager.isDatabaseReachable()) {
            String sql = "INSERT INTO prediction_history (created_at, age, gender, study_hours_per_day, " +
                    "sleep_hours, phone_usage_hours, social_media_hours, youtube_hours, gaming_hours, " +
                    "breaks_per_day, coffee_intake_mg, exercise_minutes, assignments_completed, " +
                    "attendance_percentage, stress_level, focus_score, final_grade, predicted_score, " +
                    "model_version, input_data) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setTimestamp(1, new Timestamp(result.getTimestamp()));
                ps.setInt(2, req.getAge());
                ps.setString(3, req.getGender());
                ps.setDouble(4, req.getStudyHoursPerDay());
                ps.setDouble(5, req.getSleepHours());
                ps.setDouble(6, req.getPhoneUsageHours());
                ps.setDouble(7, req.getSocialMediaHours());
                ps.setDouble(8, req.getYoutubeHours());
                ps.setDouble(9, req.getGamingHours());
                ps.setInt(10, req.getBreaksPerDay());
                ps.setDouble(11, req.getCoffeeIntakeMg());
                ps.setDouble(12, req.getExerciseMinutes());
                ps.setInt(13, req.getAssignmentsCompleted());
                ps.setDouble(14, req.getAttendancePercentage());
                ps.setInt(15, req.getStressLevel());
                ps.setDouble(16, req.getFocusScore());
                ps.setDouble(17, req.getFinalGrade());
                ps.setDouble(18, result.getPredictedScore());
                ps.setString(19, result.getModelVersion());
                ps.setString(20, jsonInput);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            record.setId(rs.getLong(1));
                        }
                    }
                    // Also mirror into local store cache
                    localStore.addRecord(record);
                    return true;
                }
            } catch (SQLException e) {
                System.err.println("Warning: MySQL insert failed, falling back to local storage: " + e.getMessage());
            }
        }

        // Fallback to local store
        localStore.addRecord(record);
        return true;
    }

    /**
     * Retrieve all prediction history records.
     */
    public List<HistoryRecord> getAllHistory() {
        if (dbManager.isDatabaseReachable()) {
            List<HistoryRecord> list = new ArrayList<>();
            String sql = "SELECT id, created_at, age, gender, study_hours_per_day, sleep_hours, " +
                    "phone_usage_hours, attendance_percentage, stress_level, focus_score, " +
                    "predicted_score, model_version, input_data FROM prediction_history ORDER BY id DESC";
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    HistoryRecord r = new HistoryRecord(
                            rs.getLong("id"),
                            rs.getTimestamp("created_at"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getDouble("study_hours_per_day"),
                            rs.getDouble("sleep_hours"),
                            rs.getDouble("phone_usage_hours"),
                            rs.getDouble("attendance_percentage"),
                            rs.getInt("stress_level"),
                            rs.getDouble("focus_score"),
                            rs.getDouble("predicted_score"),
                            rs.getString("model_version"),
                            rs.getString("input_data")
                    );
                    list.add(r);
                }
                return list;
            } catch (SQLException e) {
                System.err.println("Warning: MySQL query failed, reading local store: " + e.getMessage());
            }
        }
        return localStore.getAll();
    }

    /**
     * Filter or search history records by keyword across gender, model version, or ID.
     */
    public List<HistoryRecord> searchHistory(String keyword) {
        List<HistoryRecord> all = getAllHistory();
        if (keyword == null || keyword.trim().isEmpty()) {
            return all;
        }
        String term = keyword.trim().toLowerCase();
        List<HistoryRecord> filtered = new ArrayList<>();
        for (HistoryRecord r : all) {
            if (String.valueOf(r.getId()).contains(term) ||
                    (r.getGender() != null && r.getGender().toLowerCase().contains(term)) ||
                    (r.getModelVersion() != null && r.getModelVersion().toLowerCase().contains(term)) ||
                    String.format("%.1f", r.getPredictedScore()).contains(term)) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    /**
     * Delete a single history record by its ID.
     */
    public boolean deleteById(long id) {
        boolean mysqlDeleted = false;
        if (dbManager.isDatabaseReachable()) {
            String sql = "DELETE FROM prediction_history WHERE id = ?";
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                mysqlDeleted = ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Warning: MySQL delete failed: " + e.getMessage());
            }
        }
        boolean localDeleted = localStore.deleteById(id);
        return mysqlDeleted || localDeleted;
    }

    /**
     * Clear all prediction history records.
     */
    public boolean clearHistory() {
        boolean mysqlCleared = false;
        if (dbManager.isDatabaseReachable()) {
            String sql = "TRUNCATE TABLE prediction_history";
            try (Connection conn = dbManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                mysqlCleared = true;
            } catch (SQLException e) {
                System.err.println("Warning: MySQL truncate failed: " + e.getMessage());
            }
        }
        localStore.clear();
        return true;
    }
}
