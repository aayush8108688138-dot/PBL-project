package com.analytics.student.test;

import com.analytics.student.model.PerformanceCategory;
import com.analytics.student.model.Student;
import com.analytics.student.service.RuleBasedClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RuleBasedClassifierTest {

    private RuleBasedClassifier classifier;

    @BeforeEach
    public void setUp() {
        classifier = new RuleBasedClassifier(75.0, 50.0);
    }

    @Test
    @DisplayName("Should classify marks >= 75 as HIGH performer")
    public void testHighPerformerClassification() {
        Student s1 = new Student("STU001", "High Student", 8, 7, 95, 90, 2, 1, 85, 90.0);
        assertEquals(PerformanceCategory.HIGH, classifier.classify(s1));

        Student boundaryHigh = new Student("STU002", "Exact 75", 6, 7, 85, 85, 2, 1, 75, 75.0);
        assertEquals(PerformanceCategory.HIGH, classifier.classify(boundaryHigh));
    }

    @Test
    @DisplayName("Should classify 50 <= marks < 75 as MEDIUM performer")
    public void testMediumPerformerClassification() {
        Student s1 = new Student("STU003", "Medium Student", 5, 7, 80, 80, 4, 1, 65, 68.0);
        assertEquals(PerformanceCategory.MEDIUM, classifier.classify(s1));

        Student boundaryMed = new Student("STU004", "Exact 50", 4, 6, 70, 70, 5, 1, 50, 50.0);
        assertEquals(PerformanceCategory.MEDIUM, classifier.classify(boundaryMed));
    }

    @Test
    @DisplayName("Should classify marks < 50 as LOW performer")
    public void testLowPerformerClassification() {
        Student s1 = new Student("STU005", "Low Student", 2, 5, 50, 40, 8, 0, 40, 42.0);
        assertEquals(PerformanceCategory.LOW, classifier.classify(s1));

        Student boundaryLow = new Student("STU006", "Just below 50", 3, 5, 55, 45, 7, 0, 48, 49.9);
        assertEquals(PerformanceCategory.LOW, classifier.classify(boundaryLow));
    }

    @Test
    @DisplayName("Should respect customized threshold configurations")
    public void testCustomThresholds() {
        classifier.setHighThreshold(85.0);
        classifier.setLowThreshold(60.0);

        Student s1 = new Student("STU007", "Custom Test", 6, 7, 85, 85, 2, 1, 80, 80.0);
        // With high=85, low=60, 80 marks becomes MEDIUM
        assertEquals(PerformanceCategory.MEDIUM, classifier.classify(s1));
    }
}
