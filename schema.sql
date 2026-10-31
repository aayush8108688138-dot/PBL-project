CREATE DATABASE IF NOT EXISTS student_analytics;
USE student_analytics;

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    study_hours DECIMAL(4,1) CHECK (study_hours >= 0),
    sleep_hours DECIMAL(4,1) CHECK (sleep_hours BETWEEN 0 AND 24),
    attendance_pct DECIMAL(5,1) CHECK (attendance_pct BETWEEN 0 AND 100),
    assignment_completion_pct DECIMAL(5,1) CHECK (assignment_completion_pct BETWEEN 0 AND 100),
    screen_time_hours DECIMAL(4,1) CHECK (screen_time_hours >= 0),
    exercise_hours DECIMAL(4,1) CHECK (exercise_hours >= 0),
    previous_marks DECIMAL(5,1) CHECK (previous_marks BETWEEN 0 AND 100),
    current_marks DECIMAL(5,1) CHECK (current_marks BETWEEN 0 AND 100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
