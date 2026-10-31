package com.analytics.student.service;

import com.analytics.student.db.DataAccessException;
import com.analytics.student.db.MySqlStudentDao;
import com.analytics.student.db.StudentDao;
import com.analytics.student.db.ConnectionManager;
import com.analytics.student.model.*;
import com.analytics.student.util.CsvParser;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;
    private final AnalyticsEngine analyticsEngine;
    private Classifier classifier;
    private List<Student> cachedStudents;

    public StudentServiceImpl() {
        this.studentDao = new MySqlStudentDao();
        this.analyticsEngine = new AnalyticsEngine();
        this.classifier = new RuleBasedClassifier();
        this.cachedStudents = new ArrayList<>();
    }

    public StudentServiceImpl(StudentDao studentDao, AnalyticsEngine analyticsEngine, Classifier classifier) {
        this.studentDao = studentDao;
        this.analyticsEngine = analyticsEngine;
        this.classifier = classifier;
        this.cachedStudents = new ArrayList<>();
    }

    @Override
    public ImportSummary importAndPersistCsv(File file) throws Exception {
        ImportSummary summary = CsvParser.parseCsvFile(file);
        processAndCacheImport(summary);
        return summary;
    }

    @Override
    public ImportSummary importFromStream(InputStream inputStream) throws Exception {
        ImportSummary summary = CsvParser.parseCsvStream(inputStream);
        processAndCacheImport(summary);
        return summary;
    }

    private void processAndCacheImport(ImportSummary summary) {
        List<Student> validList = summary.getValidStudents();
        for (Student s : validList) {
            PerformanceCategory category = classifier.classify(s);
            s.setPerformanceCategory(category);
        }

        analyticsEngine.detectAnomalies(validList, 2.0);
        this.cachedStudents = new ArrayList<>(validList);

        if (isDatabaseConnected()) {
            for (Student s : validList) {
                try {
                    studentDao.insert(s);
                } catch (DataAccessException e) {
                    System.err.println("Warning: Failed to persist record to MySQL: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Student> getAllStudents() {
        if (cachedStudents.isEmpty() && isDatabaseConnected()) {
            try {
                cachedStudents = studentDao.findAll();
            } catch (DataAccessException e) {
                System.err.println("Database load failed: " + e.getMessage());
            }
        }
        return cachedStudents;
    }

    @Override
    public List<Student> getStudentsByCategory(PerformanceCategory category) {
        return getAllStudents().stream()
                .filter(s -> s.getPerformanceCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public Student getStudentById(String studentId) {
        if (studentId == null) return null;
        return getAllStudents().stream()
                .filter(s -> studentId.equalsIgnoreCase(s.getStudentId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void saveStudent(Student student) {
        if (student == null) return;

        PerformanceCategory cat = classifier.classify(student);
        student.setPerformanceCategory(cat);

        int index = -1;
        for (int i = 0; i < cachedStudents.size(); i++) {
            if (cachedStudents.get(i).getStudentId().equalsIgnoreCase(student.getStudentId())) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            cachedStudents.set(index, student);
        } else {
            cachedStudents.add(student);
        }

        if (isDatabaseConnected()) {
            try {
                studentDao.update(student);
            } catch (DataAccessException e) {
                System.err.println("Warning: Failed to update student in MySQL: " + e.getMessage());
            }
        }
    }

    @Override
    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
        reclassifyAllStudents();
    }

    @Override
    public void reclassifyAllStudents() {
        for (Student s : cachedStudents) {
            s.setPerformanceCategory(classifier.classify(s));
        }
        analyticsEngine.detectAnomalies(cachedStudents, 2.0);
    }

    @Override
    public Map<String, Double> getClassAverages() {
        return analyticsEngine.calculateClassAverages(getAllStudents());
    }

    @Override
    public Map<String, Double> getMetricDistribution(String metricName) {
        return analyticsEngine.calculateMetricDistribution(getAllStudents(), metricName);
    }

    @Override
    public List<GroupComparison> getGroupComparisons() {
        return analyticsEngine.calculateGroupComparisons(getAllStudents());
    }

    @Override
    public CorrelationResult calculateCorrelation(String varX, String varY) {
        return analyticsEngine.calculateCorrelation(getAllStudents(), varX, varY);
    }

    @Override
    public List<AnomalyResult> detectAnomalies(double zScoreThreshold) {
        return analyticsEngine.detectAnomalies(getAllStudents(), zScoreThreshold);
    }

    @Override
    public List<Student> filterStudents(PerformanceCategory category, double minStudy, double maxStudy, double minAttendance, double maxAttendance) {
        return analyticsEngine.filterStudents(getAllStudents(), category, minStudy, maxStudy, minAttendance, maxAttendance);
    }

    @Override
    public boolean isDatabaseConnected() {
        try (Connection conn = ConnectionManager.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public AnalyticsEngine getAnalyticsEngine() {
        return analyticsEngine;
    }
}
