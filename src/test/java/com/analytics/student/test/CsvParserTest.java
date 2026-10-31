package com.analytics.student.test;

import com.analytics.student.exception.MalformedRowException;
import com.analytics.student.exception.MissingFieldException;
import com.analytics.student.exception.OutOfRangeException;
import com.analytics.student.model.ImportSummary;
import com.analytics.student.model.Student;
import com.analytics.student.util.CsvParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CsvParserTest {

    @Test
    @DisplayName("Should parse valid student CSV row correctly")
    public void testValidRowParsing() throws Exception {
        String validRow = "STU001,Aarav Sharma,7.5,7.0,92.0,95.0,2.5,1.5,82.0,88.5";
        Student s = CsvParser.parseRow(validRow, 1);

        assertNotNull(s);
        assertEquals("STU001", s.getStudentId());
        assertEquals("Aarav Sharma", s.getName());
        assertEquals(7.5, s.getStudyHours());
        assertEquals(7.0, s.getSleepHours());
        assertEquals(92.0, s.getAttendance());
        assertEquals(95.0, s.getAssignmentCompletion());
        assertEquals(2.5, s.getScreenTime());
        assertEquals(1.5, s.getExerciseHours());
        assertEquals(82.0, s.getPreviousMarks());
        assertEquals(88.5, s.getCurrentMarks());
    }

    @Test
    @DisplayName("Should parse extreme boundary values (0 study hours, 24 sleep hours, 0% & 100% marks)")
    public void testBoundaryValues() throws Exception {
        String boundaryRow = "STU002,Boundary Test,0.0,24.0,100.0,0.0,24.0,0.0,0.0,100.0";
        Student s = CsvParser.parseRow(boundaryRow, 2);

        assertNotNull(s);
        assertEquals(0.0, s.getStudyHours());
        assertEquals(24.0, s.getSleepHours());
        assertEquals(100.0, s.getAttendance());
        assertEquals(0.0, s.getAssignmentCompletion());
        assertEquals(24.0, s.getScreenTime());
        assertEquals(0.0, s.getExerciseHours());
        assertEquals(0.0, s.getPreviousMarks());
        assertEquals(100.0, s.getCurrentMarks());
    }

    @Test
    @DisplayName("Should throw MissingFieldException when student ID or name is blank")
    public void testMissingFieldException() {
        String missingIdRow = ",No ID Student,5.0,7.0,80.0,85.0,3.0,1.0,70.0,75.0";
        assertThrows(MissingFieldException.class, () -> CsvParser.parseRow(missingIdRow, 3));

        String missingNameRow = "STU003,,5.0,7.0,80.0,85.0,3.0,1.0,70.0,75.0";
        assertThrows(MissingFieldException.class, () -> CsvParser.parseRow(missingNameRow, 4));
    }

    @Test
    @DisplayName("Should throw OutOfRangeException for metrics outside allowed bounds")
    public void testOutOfRangeException() {
        // Sleep hours > 24
        String invalidSleep = "STU004,Invalid Sleep,6.0,26.0,90.0,90.0,2.0,1.0,80.0,85.0";
        assertThrows(OutOfRangeException.class, () -> CsvParser.parseRow(invalidSleep, 5));

        // Attendance > 100%
        String invalidAtt = "STU005,Invalid Attendance,6.0,7.0,115.0,90.0,2.0,1.0,80.0,85.0";
        assertThrows(OutOfRangeException.class, () -> CsvParser.parseRow(invalidAtt, 6));

        // Negative study hours
        String negativeStudy = "STU006,Negative Study,-2.0,7.0,80.0,90.0,2.0,1.0,80.0,85.0";
        assertThrows(OutOfRangeException.class, () -> CsvParser.parseRow(negativeStudy, 7));
    }

    @Test
    @DisplayName("Should throw MalformedRowException for insufficient columns or non-numeric tokens")
    public void testMalformedRowException() {
        // Too few columns
        String incompleteRow = "STU007,Incomplete,5.0,7.0,80.0";
        assertThrows(MalformedRowException.class, () -> CsvParser.parseRow(incompleteRow, 8));

        // Non-numeric text in double field
        String nonNumeric = "STU008,Bad Number,abc,7.0,80.0,90.0,2.0,1.0,80.0,85.0";
        assertThrows(MalformedRowException.class, () -> CsvParser.parseRow(nonNumeric, 9));
    }

    @Test
    @DisplayName("Should parse multi-line CSV stream separating valid records from errors")
    public void testCsvStreamParsing() throws IOException {
        String csvData =
                "student_id,name,study_hours,sleep_hours,attendance,assignment_completion,screen_time,exercise,previous_marks,current_marks\n" +
                "STU001,Valid Student One,8.0,7.5,95.0,90.0,2.0,1.0,85.0,90.0\n" +
                "STU002,Invalid Sleep Student,6.0,28.0,80.0,85.0,3.0,1.0,70.0,75.0\n" +
                "STU003,Valid Student Two,6.0,7.0,85.0,88.0,3.0,1.5,75.0,80.0\n" +
                "STU004,,5.0,7.0,80.0,85.0,3.0,1.0,70.0,75.0\n";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        ImportSummary summary = CsvParser.parseCsvStream(inputStream);

        assertNotNull(summary);
        assertEquals(2, summary.getSuccessCount());
        assertEquals(2, summary.getErrorCount());
        assertEquals(5, summary.getTotalRowsProcessed());
    }
}
