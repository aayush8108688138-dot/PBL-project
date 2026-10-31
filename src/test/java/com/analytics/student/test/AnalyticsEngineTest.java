package com.analytics.student.test;

import com.analytics.student.model.*;
import com.analytics.student.service.AnalyticsEngine;
import com.analytics.student.service.RuleBasedClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyticsEngineTest {

    private AnalyticsEngine engine;
    private List<Student> testStudents;

    @BeforeEach
    public void setUp() {
        engine = new AnalyticsEngine();
        testStudents = new ArrayList<>();

        RuleBasedClassifier classifier = new RuleBasedClassifier();

        Student s1 = new Student("STU001", "Alice", 9.0, 8.0, 95.0, 95.0, 2.0, 2.0, 85.0, 92.0);
        Student s2 = new Student("STU002", "Bob", 7.0, 7.0, 85.0, 85.0, 3.0, 1.0, 75.0, 78.0);
        Student s3 = new Student("STU003", "Charlie", 5.0, 6.5, 75.0, 70.0, 5.0, 1.0, 60.0, 64.0);
        Student s4 = new Student("STU004", "David", 3.0, 5.0, 55.0, 45.0, 8.0, 0.5, 45.0, 42.0);

        // Synthetic Anomaly student (14h study hours but 38 marks)
        Student s5 = new Student("STU005", "Eve Anomaly", 14.0, 4.0, 30.0, 30.0, 12.0, 0.0, 50.0, 38.0);

        testStudents.add(s1);
        testStudents.add(s2);
        testStudents.add(s3);
        testStudents.add(s4);
        testStudents.add(s5);

        for (Student s : testStudents) {
            s.setPerformanceCategory(classifier.classify(s));
        }
    }

    @Test
    @DisplayName("Should correctly calculate class averages using Streams API")
    public void testClassAverages() {
        Map<String, Double> avgs = engine.calculateClassAverages(testStudents);

        assertNotNull(avgs);
        // (9+7+5+3+14)/5 = 38/5 = 7.6
        assertEquals(7.6, avgs.get(AnalyticsEngine.METRIC_STUDY_HOURS), 0.01);
        // (92+78+64+42+38)/5 = 314/5 = 62.8
        assertEquals(62.8, avgs.get(AnalyticsEngine.METRIC_CURR_MARKS), 0.01);
    }

    @Test
    @DisplayName("Should calculate Pearson Correlation and MUST include non-causation caveat")
    public void testPearsonCorrelationAndCaveat() {
        CorrelationResult res = engine.calculateCorrelation(testStudents, AnalyticsEngine.METRIC_STUDY_HOURS, AnalyticsEngine.METRIC_CURR_MARKS);

        assertNotNull(res);
        assertNotNull(res.getCausationCaveat());
        assertTrue(res.getCausationCaveat().contains("Correlation does not imply causation"));
        assertNotNull(res.getStrengthInterpretation());
        assertEquals(5, res.getSampleSize());
    }

    @Test
    @DisplayName("Should aggregate lifestyle metrics by performance category")
    public void testGroupComparisons() {
        List<GroupComparison> comparisons = engine.calculateGroupComparisons(testStudents);

        assertNotNull(comparisons);
        assertEquals(3, comparisons.size()); // HIGH, MEDIUM, LOW

        GroupComparison highGroup = comparisons.stream()
                .filter(c -> c.getCategory() == PerformanceCategory.HIGH)
                .findFirst().orElse(null);

        assertNotNull(highGroup);
        assertEquals(2, highGroup.getStudentCount()); // s1 (92) & s2 (78)
        assertTrue(highGroup.getAvgStudyHours() > 7.0);
    }

    @Test
    @DisplayName("Should detect outlier students deviating from class norms via Z-score threshold")
    public void testAnomalyDetection() {
        List<AnomalyResult> anomalies = engine.detectAnomalies(testStudents, 1.8);

        assertNotNull(anomalies);
        assertFalse(anomalies.isEmpty());

        // Eve Anomaly (STU005) has 14h study hours, 12h screen time, 38 marks
        boolean eveFlagged = anomalies.stream()
                .anyMatch(a -> a.getStudent().getStudentId().equals("STU005"));

        assertTrue(eveFlagged, "Eve (STU005) should be detected as a statistical anomaly");
    }
}
