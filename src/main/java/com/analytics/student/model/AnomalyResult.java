package com.analytics.student.model;

import java.util.List;

/**
 * Data transfer object representing a student identified as an anomaly/outlier.
 */
public class AnomalyResult {

    private final Student student;
    private final double maxZScore;
    private final List<String> anomalousMetrics;
    private final String explanation;

    public AnomalyResult(Student student, double maxZScore, List<String> anomalousMetrics, String explanation) {
        this.student = student;
        this.maxZScore = maxZScore;
        this.anomalousMetrics = anomalousMetrics;
        this.explanation = explanation;
    }

    public Student getStudent() {
        return student;
    }

    public double getMaxZScore() {
        return maxZScore;
    }

    public List<String> getAnomalousMetrics() {
        return anomalousMetrics;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return String.format("AnomalyResult[Student='%s' (%s), Max Z-Score=%.2f, Metrics=%s, Reason='%s']",
                student.getName(), student.getStudentId(), maxZScore, anomalousMetrics, explanation);
    }
}
