package com.analytics.student.test;

import com.analytics.student.model.Student;
import com.analytics.student.service.ClassLevelReportGenerator;
import com.analytics.student.service.IndividualStudentReportGenerator;
import com.analytics.student.service.RuleBasedClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    private List<Student> students;
    private Student targetStudent;

    @BeforeEach
    public void setUp() {
        RuleBasedClassifier classifier = new RuleBasedClassifier();

        targetStudent = new Student("STU001", "Aarav Sharma", 8.0, 7.0, 95.0, 90.0, 2.0, 1.5, 82.0, 88.0);
        targetStudent.setPerformanceCategory(classifier.classify(targetStudent));

        Student s2 = new Student("STU002", "Rohan Patel", 4.0, 6.0, 70.0, 65.0, 5.5, 0.5, 60.0, 58.0);
        s2.setPerformanceCategory(classifier.classify(s2));

        students = List.of(targetStudent, s2);
    }

    @Test
    @DisplayName("Individual student report generator should produce formatted string containing causation caveat")
    public void testIndividualReportGeneration() {
        IndividualStudentReportGenerator generator = new IndividualStudentReportGenerator(students, targetStudent);
        String report = generator.generateReport();

        assertNotNull(report);
        assertTrue(report.contains("INDIVIDUAL STUDENT PERFORMANCE ANALYTICS CARD"));
        assertTrue(report.contains("Aarav Sharma"));
        assertTrue(report.contains("STU001"));
        assertTrue(report.contains("Correlation does not imply causation"));
    }

    @Test
    @DisplayName("Class-level report generator should produce summary statistics and correlation matrix")
    public void testClassLevelReportGeneration() {
        ClassLevelReportGenerator generator = new ClassLevelReportGenerator(students);
        String report = generator.generateReport();

        assertNotNull(report);
        assertTrue(report.contains("CLASS-LEVEL STUDENT LIFESTYLE & ANALYTICS REPORT"));
        assertTrue(report.contains("PEARSON CORRELATION ANALYSIS"));
        assertTrue(report.contains("Correlation does not imply causation"));
    }
}
