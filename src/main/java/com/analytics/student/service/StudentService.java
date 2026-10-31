package com.analytics.student.service;

import com.analytics.student.model.*;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Service interface orchestrating data import, database persistence, analytics processing, and reporting.
 */
public interface StudentService {

    ImportSummary importAndPersistCsv(File file) throws Exception;

    ImportSummary importFromStream(java.io.InputStream inputStream) throws Exception;

    List<Student> getAllStudents();

    List<Student> getStudentsByCategory(PerformanceCategory category);

    Student getStudentById(String studentId);

    void saveStudent(Student student);

    void setClassifier(Classifier classifier);

    void reclassifyAllStudents();

    Map<String, Double> getClassAverages();

    Map<String, Double> getMetricDistribution(String metricName);

    List<GroupComparison> getGroupComparisons();

    CorrelationResult calculateCorrelation(String varX, String varY);

    List<AnomalyResult> detectAnomalies(double zScoreThreshold);

    List<Student> filterStudents(PerformanceCategory category, double minStudy, double maxStudy, double minAttendance, double maxAttendance);

    boolean isDatabaseConnected();

    AnalyticsEngine getAnalyticsEngine();
}
