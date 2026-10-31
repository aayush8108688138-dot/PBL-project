package com.analytics.student.db;

import com.analytics.student.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlStudentDaoTest {

    private MySqlStudentDao dao;
    private final String TEST_ID = "T-99999";

    @BeforeEach
    public void setup() {
        dao = new MySqlStudentDao();
        // Clean up before test just in case a previous run failed to clean up
        try {
            dao.delete(TEST_ID);
        } catch (Exception ignored) {}
    }

    @AfterEach
    public void teardown() {
        // Clean up after test
        try {
            dao.delete(TEST_ID);
        } catch (Exception ignored) {}
    }

    @Test
    public void testInsertAndReadStudent() {
        // Create a test student
        Student student = new Student();
        student.setStudentId(TEST_ID);
        student.setName("Test Student");
        student.setStudyHours(5.5);
        student.setSleepHours(7.0);
        student.setAttendance(85.0);
        student.setAssignmentCompletion(90.0);
        student.setScreenTime(3.5);
        student.setExerciseHours(1.5);
        student.setPreviousMarks(75.0);
        student.setCurrentMarks(80.0);

        // Insert into DB
        dao.insert(student);

        // Read it back
        Optional<Student> fetchedOpt = dao.findById(TEST_ID);
        assertTrue(fetchedOpt.isPresent(), "Student should be found in DB after insertion");

        Student fetched = fetchedOpt.get();
        assertEquals("Test Student", fetched.getName());
        assertEquals(5.5, fetched.getStudyHours());
        assertEquals(85.0, fetched.getAttendance());
        assertEquals(80.0, fetched.getCurrentMarks());
        
        System.out.println("TEST SUCCESS: Successfully inserted, verified, and cleaned up test record " + TEST_ID);
    }
}
