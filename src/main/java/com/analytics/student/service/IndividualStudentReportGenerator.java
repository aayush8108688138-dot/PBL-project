package com.analytics.student.service;

import com.analytics.student.model.CorrelationResult;
import com.analytics.student.model.Student;

import java.util.List;
import java.util.Map;

/**
 * Report generator for producing detailed individual student lifestyle and
 * performance cards.
 */
public class IndividualStudentReportGenerator extends ReportGenerator {

    private final Student student;
    private final AnalyticsEngine analyticsEngine;

    public IndividualStudentReportGenerator(List<Student> classList, Student student) {
        super(classList);
        this.student = student;
        this.analyticsEngine = new AnalyticsEngine();
    }

    @Override
    public String generateReport() {
        if (student == null) {
            return "ERROR: No student selected for individual report generation.";
        }

        Map<String, Double> classAvgs = analyticsEngine.calculateClassAverages(students);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("               INDIVIDUAL STUDENT PERFORMANCE ANALYTICS CARD            \n");
        sb.append("========================================================================\n");
        sb.append(String.format(" Generated Date : %s\n", getFormattedTimestamp()));
        sb.append(String.format(" Student ID     : %s\n", student.getStudentId()));
        sb.append(String.format(" Full Name      : %s\n", student.getName()));
        sb.append(String.format(" Classification : %s (%s)\n",
                student.getPerformanceCategory().getDisplayName(),
                student.getPerformanceCategory().getCriteriaDescription()));
        sb.append(String.format(" Anomaly Status : %s %s\n",
                student.isAnomaly() ? "[ALERT: OUTLIER DETECTED]" : "[NORMAL]",
                student.isAnomaly() ? "(" + student.getAnomalyDetails() + ")" : ""));
        sb.append("------------------------------------------------------------------------\n\n");

        sb.append("1. LIFESTYLE & ACADEMIC METRICS COMPARISON VS CLASS AVERAGE\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(String.format(" %-28s | %-15s | %-15s | %-12s\n", "Metric Factor", "Student Value", "Class Average",
                "Delta"));
        sb.append("------------------------------------------------------------------------\n");

        appendMetricRow(sb, "Study Hours (daily)", student.getStudyHours(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_STUDY_HOURS, 0.0), "hrs");
        appendMetricRow(sb, "Sleep Hours (daily)", student.getSleepHours(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_SLEEP_HOURS, 0.0), "hrs");
        appendMetricRow(sb, "Attendance (%)", student.getAttendance(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_ATTENDANCE, 0.0), "%");
        appendMetricRow(sb, "Assignment Completion (%)", student.getAssignmentCompletion(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_ASSIGNMENT, 0.0), "%");
        appendMetricRow(sb, "Screen Time (daily)", student.getScreenTime(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_SCREEN_TIME, 0.0), "hrs");
        appendMetricRow(sb, "Exercise Hours (daily)", student.getExerciseHours(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_EXERCISE, 0.0), "hrs");
        appendMetricRow(sb, "Previous Semester Marks", student.getPreviousMarks(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_PREV_MARKS, 0.0), "marks");
        appendMetricRow(sb, "Current Semester Marks", student.getCurrentMarks(),
                classAvgs.getOrDefault(AnalyticsEngine.METRIC_CURR_MARKS, 0.0), "marks");
        sb.append("------------------------------------------------------------------------\n\n");

        sb.append("2. PERSONALIZED INSIGHTS & RECOMMENDATIONS\n");
        sb.append("------------------------------------------------------------------------\n");
        if (student.getStudyHours() < classAvgs.getOrDefault(AnalyticsEngine.METRIC_STUDY_HOURS, 6.0)) {
            sb.append(
                    " * Study Habit Warning: Daily study hours are below the class average. Consider increasing structured focus time.\n");
        } else {
            sb.append(" * Study Habit Strength: Above-average dedicated study time maintained.\n");
        }

        if (student.getSleepHours() < 6.0) {
            sb.append(
                    " * Sleep Quality Warning: Less than 6 hours sleep daily may impede memory consolidation and current mark potential.\n");
        }

        if (student.getScreenTime() > 5.0) {
            sb.append(" * High Screen Time Alert: Daily recreational screen time exceeds 5 hours.\n");
        }

        if (student.getAttendance() < 75.0) {
            sb.append(" * Attendance Warning: Attendance is below 75% threshold, risking academic eligibility.\n");
        }
        sb.append("\n");

        sb.append("3. MANDATORY SCIENTIFIC & ANALYTICAL CAVEAT\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" ");
        sb.append(CorrelationResult.MANDATORY_CAUSATION_CAVEAT);
        sb.append("\n========================================================================\n");

        return sb.toString();
    }

    private void appendMetricRow(StringBuilder sb, String metricName, double studentVal, double classAvg, String unit) {
        double delta = studentVal - classAvg;
        String deltaStr = String.format("%s%.2f %s", delta >= 0 ? "+" : "", delta, unit);
        sb.append(String.format(" %-28s | %-15s | %-15s | %-12s\n",
                metricName,
                String.format("%.2f %s", studentVal, unit),
                String.format("%.2f %s", classAvg, unit),
                deltaStr));
    }
}
