-- ====================================================================
-- DATABASE SCHEMA: Student Productivity Predictor
-- Database: student_productivity_db
-- Description: Stores inference history and user lifestyle inputs
-- ====================================================================

CREATE DATABASE IF NOT EXISTS student_productivity_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_productivity_db;

CREATE TABLE IF NOT EXISTS prediction_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    study_hours_per_day DOUBLE NOT NULL,
    sleep_hours DOUBLE NOT NULL,
    phone_usage_hours DOUBLE NOT NULL,
    social_media_hours DOUBLE NOT NULL,
    youtube_hours DOUBLE NOT NULL,
    gaming_hours DOUBLE NOT NULL,
    breaks_per_day INT NOT NULL,
    coffee_intake_mg DOUBLE NOT NULL,
    exercise_minutes DOUBLE NOT NULL,
    assignments_completed INT NOT NULL,
    attendance_percentage DOUBLE NOT NULL,
    stress_level INT NOT NULL,
    focus_score DOUBLE NOT NULL,
    final_grade DOUBLE NOT NULL,
    predicted_score DOUBLE NOT NULL,
    model_version VARCHAR(50) DEFAULT 'RandomForest-v1.0',
    input_data JSON NULL,
    INDEX idx_created_at (created_at),
    INDEX idx_predicted_score (predicted_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
