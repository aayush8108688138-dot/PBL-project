package com.analytics.student.db;

import com.analytics.student.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySqlStudentDao implements StudentDao {

    @Override
    public void insert(Student s) {
        String sql = "INSERT INTO students (student_id, name, study_hours, sleep_hours, attendance_pct, " +
                "assignment_completion_pct, screen_time_hours, exercise_hours, previous_marks, current_marks) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), study_hours = VALUES(study_hours), " +
                "sleep_hours = VALUES(sleep_hours), attendance_pct = VALUES(attendance_pct), " +
                "assignment_completion_pct = VALUES(assignment_completion_pct), screen_time_hours = VALUES(screen_time_hours), " +
                "exercise_hours = VALUES(exercise_hours), previous_marks = VALUES(previous_marks), current_marks = VALUES(current_marks)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStudentParameters(stmt, s);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to insert/update student with ID: " + s.getStudentId(), ex);
        }
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to fetch all students.", ex);
        }
        return students;
    }

    @Override
    public Optional<Student> findById(String id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to fetch student with ID: " + id, ex);
        }
        return Optional.empty();
    }

    @Override
    public void update(Student s) {
        // Handled via insert ON DUPLICATE KEY UPDATE, but implementing explicit update here.
        String sql = "UPDATE students SET name=?, study_hours=?, sleep_hours=?, attendance_pct=?, " +
                "assignment_completion_pct=?, screen_time_hours=?, exercise_hours=?, previous_marks=?, current_marks=? " +
                "WHERE student_id=?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getName());
            stmt.setDouble(2, s.getStudyHours());
            stmt.setDouble(3, s.getSleepHours());
            stmt.setDouble(4, s.getAttendance());
            stmt.setDouble(5, s.getAssignmentCompletion());
            stmt.setDouble(6, s.getScreenTime());
            stmt.setDouble(7, s.getExerciseHours());
            stmt.setDouble(8, s.getPreviousMarks());
            stmt.setDouble(9, s.getCurrentMarks());
            stmt.setString(10, s.getStudentId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update student with ID: " + s.getStudentId(), ex);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete student with ID: " + id, ex);
        }
    }

    @Override
    public List<Student> findByPerformanceGroup(String group) {
        // Group logic is calculated in memory typically, but if we need a DB-level filter, we'd add a column.
        throw new UnsupportedOperationException("Not implemented at DB level yet.");
    }

    private void setStudentParameters(PreparedStatement stmt, Student s) throws SQLException {
        stmt.setString(1, s.getStudentId());
        stmt.setString(2, s.getName());
        stmt.setDouble(3, s.getStudyHours());
        stmt.setDouble(4, s.getSleepHours());
        stmt.setDouble(5, s.getAttendance());
        stmt.setDouble(6, s.getAssignmentCompletion());
        stmt.setDouble(7, s.getScreenTime());
        stmt.setDouble(8, s.getExerciseHours());
        stmt.setDouble(9, s.getPreviousMarks());
        stmt.setDouble(10, s.getCurrentMarks());
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getDouble("study_hours"),
                rs.getDouble("sleep_hours"),
                rs.getDouble("attendance_pct"),
                rs.getDouble("assignment_completion_pct"),
                rs.getDouble("screen_time_hours"),
                rs.getDouble("exercise_hours"),
                rs.getDouble("previous_marks"),
                rs.getDouble("current_marks")
        );
    }
}
