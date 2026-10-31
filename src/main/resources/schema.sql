-- =============================================================
-- MySQL Database DDL Script for Student Analytics System
-- Project: Student Lifestyle & Academic Performance Analytics
-- =============================================================

CREATE DATABASE IF NOT EXISTS student_analytics;
USE student_analytics;

DROP TABLE IF EXISTS students;

CREATE TABLE students (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    study_hours DOUBLE NOT NULL,
    sleep_hours DOUBLE NOT NULL,
    attendance DOUBLE NOT NULL,
    assignment_completion DOUBLE NOT NULL,
    screen_time DOUBLE NOT NULL,
    exercise_hours DOUBLE NOT NULL,
    previous_marks DOUBLE NOT NULL,
    current_marks DOUBLE NOT NULL,
    performance_category VARCHAR(15) NOT NULL,
    is_anomaly BOOLEAN DEFAULT FALSE,
    anomaly_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
