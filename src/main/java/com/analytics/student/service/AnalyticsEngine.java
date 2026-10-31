package com.analytics.student.service;

import com.analytics.student.model.*;
import com.analytics.student.util.CorrelationCalculator;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Descriptive statistics, correlation analysis, and anomaly detection engine
 * leveraging Java Streams API.
 */
public class AnalyticsEngine {

    public static final String METRIC_STUDY_HOURS = "Study Hours";
    public static final String METRIC_SLEEP_HOURS = "Sleep Hours";
    public static final String METRIC_ATTENDANCE = "Attendance (%)";
    public static final String METRIC_ASSIGNMENT = "Assignment Completion (%)";
    public static final String METRIC_SCREEN_TIME = "Screen Time";
    public static final String METRIC_EXERCISE = "Exercise Hours";
    public static final String METRIC_PREV_MARKS = "Previous Marks";
    public static final String METRIC_CURR_MARKS = "Current Marks";

    private final Map<String, ToDoubleFunction<Student>> metricExtractors = new LinkedHashMap<>();

    public AnalyticsEngine() {
        metricExtractors.put(METRIC_STUDY_HOURS, Student::getStudyHours);
        metricExtractors.put(METRIC_SLEEP_HOURS, Student::getSleepHours);
        metricExtractors.put(METRIC_ATTENDANCE, Student::getAttendance);
        metricExtractors.put(METRIC_ASSIGNMENT, Student::getAssignmentCompletion);
        metricExtractors.put(METRIC_SCREEN_TIME, Student::getScreenTime);
        metricExtractors.put(METRIC_EXERCISE, Student::getExerciseHours);
        metricExtractors.put(METRIC_PREV_MARKS, Student::getPreviousMarks);
        metricExtractors.put(METRIC_CURR_MARKS, Student::getCurrentMarks);
    }

    public Set<String> getAvailableMetrics() {
        return metricExtractors.keySet();
    }

    public ToDoubleFunction<Student> getExtractor(String metricName) {
        return metricExtractors.getOrDefault(metricName, Student::getCurrentMarks);
    }

    /**
     * Calculates class average for all lifestyle and performance factors using
     * Streams.
     */
    public Map<String, Double> calculateClassAverages(List<Student> students) {
        Map<String, Double> averages = new LinkedHashMap<>();
        if (students == null || students.isEmpty()) {
            metricExtractors.keySet().forEach(m -> averages.put(m, 0.0));
            return averages;
        }

        for (Map.Entry<String, ToDoubleFunction<Student>> entry : metricExtractors.entrySet()) {
            double avg = students.stream()
                    .mapToDouble(entry.getValue())
                    .average()
                    .orElse(0.0);
            averages.put(entry.getKey(), Math.round(avg * 100.0) / 100.0);
        }

        return averages;
    }

    /**
     * Calculates summary metrics (count, mean, stdDev, min, max) for a specific
     * factor.
     */
    public Map<String, Double> calculateMetricDistribution(List<Student> students, String metricName) {
        Map<String, Double> dist = new LinkedHashMap<>();
        ToDoubleFunction<Student> extractor = getExtractor(metricName);

        if (students == null || students.isEmpty()) {
            dist.put("Count", 0.0);
            dist.put("Mean", 0.0);
            dist.put("StdDev", 0.0);
            dist.put("Min", 0.0);
            dist.put("Max", 0.0);
            return dist;
        }

        DoubleSummaryStatistics stats = students.stream().collect(Collectors.summarizingDouble(extractor));
        double mean = stats.getAverage();

        double variance = students.stream()
                .mapToDouble(s -> Math.pow(extractor.applyAsDouble(s) - mean, 2))
                .average()
                .orElse(0.0);
        double stdDev = Math.sqrt(variance);

        dist.put("Count", (double) stats.getCount());
        dist.put("Mean", Math.round(mean * 100.0) / 100.0);
        dist.put("StdDev", Math.round(stdDev * 100.0) / 100.0);
        dist.put("Min", Math.round(stats.getMin() * 100.0) / 100.0);
        dist.put("Max", Math.round(stats.getMax() * 100.0) / 100.0);

        return dist;
    }

    /**
     * Compares lifestyle indicators across HIGH, MEDIUM, and LOW performance
     * groups.
     */
    public List<GroupComparison> calculateGroupComparisons(List<Student> students) {
        List<GroupComparison> comparisons = new ArrayList<>();
        if (students == null || students.isEmpty()) {
            return comparisons;
        }

        Map<PerformanceCategory, List<Student>> grouped = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getPerformanceCategory() != null ? s.getPerformanceCategory()
                                : PerformanceCategory.MEDIUM));

        for (PerformanceCategory cat : PerformanceCategory.values()) {
            List<Student> groupStudents = grouped.getOrDefault(cat, Collections.emptyList());
            if (groupStudents.isEmpty()) {
                comparisons.add(new GroupComparison(cat, 0, 0, 0, 0, 0, 0, 0, 0, 0));
                continue;
            }

            int count = groupStudents.size();
            double avgStudy = groupStudents.stream().mapToDouble(Student::getStudyHours).average().orElse(0.0);
            double avgSleep = groupStudents.stream().mapToDouble(Student::getSleepHours).average().orElse(0.0);
            double avgAtt = groupStudents.stream().mapToDouble(Student::getAttendance).average().orElse(0.0);
            double avgAssign = groupStudents.stream().mapToDouble(Student::getAssignmentCompletion).average()
                    .orElse(0.0);
            double avgScreen = groupStudents.stream().mapToDouble(Student::getScreenTime).average().orElse(0.0);
            double avgEx = groupStudents.stream().mapToDouble(Student::getExerciseHours).average().orElse(0.0);
            double avgPrev = groupStudents.stream().mapToDouble(Student::getPreviousMarks).average().orElse(0.0);
            double avgCurr = groupStudents.stream().mapToDouble(Student::getCurrentMarks).average().orElse(0.0);

            comparisons.add(new GroupComparison(
                    cat, count,
                    round(avgStudy), round(avgSleep), round(avgAtt),
                    round(avgAssign), round(avgScreen), round(avgEx),
                    round(avgPrev), round(avgCurr)));
        }

        return comparisons;
    }

    /**
     * Computes Pearson correlation between two chosen variables.
     */
    public CorrelationResult calculateCorrelation(List<Student> students, String varXLabel, String varYLabel) {
        ToDoubleFunction<Student> extX = getExtractor(varXLabel);
        ToDoubleFunction<Student> extY = getExtractor(varYLabel);
        return CorrelationCalculator.calculatePearson(students, varXLabel, extX, varYLabel, extY);
    }

    /**
     * Detects students who deviate significantly from class norms using Z-score
     * threshold.
     */
    public List<AnomalyResult> detectAnomalies(List<Student> students, double zScoreThreshold) {
        List<AnomalyResult> anomalies = new ArrayList<>();
        if (students == null || students.isEmpty()) {
            return anomalies;
        }

        // Compute means and standard deviations for all metrics
        Map<String, Double> means = new HashMap<>();
        Map<String, Double> stdDevs = new HashMap<>();

        for (Map.Entry<String, ToDoubleFunction<Student>> entry : metricExtractors.entrySet()) {
            String mName = entry.getKey();
            ToDoubleFunction<Student> extractor = entry.getValue();

            double mean = students.stream().mapToDouble(extractor).average().orElse(0.0);
            double var = students.stream().mapToDouble(s -> Math.pow(extractor.applyAsDouble(s) - mean, 2)).average()
                    .orElse(0.0);
            double sd = Math.sqrt(var);

            means.put(mName, mean);
            stdDevs.put(mName, sd);
        }

        for (Student s : students) {
            List<String> anomalousMetrics = new ArrayList<>();
            double maxZ = 0.0;

            for (Map.Entry<String, ToDoubleFunction<Student>> entry : metricExtractors.entrySet()) {
                String mName = entry.getKey();
                double val = entry.getValue().applyAsDouble(s);
                double mean = means.get(mName);
                double sd = stdDevs.get(mName);

                if (sd > 0.0001) {
                    double zScore = Math.abs(val - mean) / sd;
                    if (zScore >= zScoreThreshold) {
                        anomalousMetrics.add(String.format("%s (z=%.1f)", mName, zScore));
                        if (zScore > maxZ) {
                            maxZ = zScore;
                        }
                    }
                }
            }

            // Also check domain-logic anomalies (e.g. High Study Hours >= 9h but Current
            // Marks < 45)
            if (s.getStudyHours() >= 9.0 && s.getCurrentMarks() < 45.0) {
                if (!anomalousMetrics.contains("High Study / Low Marks")) {
                    anomalousMetrics.add("High Study / Low Marks");
                    if (maxZ < 2.5)
                        maxZ = 2.5;
                }
            }

            if (!anomalousMetrics.isEmpty()) {
                String explanation = String.join(" | ", anomalousMetrics);
                s.setAnomaly(true);
                s.setAnomalyDetails(explanation);
                anomalies.add(new AnomalyResult(s, round(maxZ), anomalousMetrics, explanation));
            } else {
                s.setAnomaly(false);
                s.setAnomalyDetails("");
            }
        }

        return anomalies;
    }

    /**
     * Filters student list by performance category and lifestyle ranges.
     */
    public List<Student> filterStudents(List<Student> students, PerformanceCategory category,
            double minStudy, double maxStudy,
            double minAttendance, double maxAttendance) {
        if (students == null)
            return Collections.emptyList();

        return students.stream()
                .filter(s -> category == null || s.getPerformanceCategory() == category)
                .filter(s -> s.getStudyHours() >= minStudy && s.getStudyHours() <= maxStudy)
                .filter(s -> s.getAttendance() >= minAttendance && s.getAttendance() <= maxAttendance)
                .collect(Collectors.toList());
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
