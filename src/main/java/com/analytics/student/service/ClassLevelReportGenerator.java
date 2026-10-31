package com.analytics.student.service;

import com.analytics.student.model.AnomalyResult;
import com.analytics.student.model.CorrelationResult;
import com.analytics.student.model.GroupComparison;
import com.analytics.student.model.Student;

import java.util.List;
import java.util.Map;

/**
 * Report generator for producing comprehensive class-level analytical summaries.
 */
public class ClassLevelReportGenerator extends ReportGenerator {

    private final AnalyticsEngine analyticsEngine;

    public ClassLevelReportGenerator(List<Student> classList) {
        super(classList);
        this.analyticsEngine = new AnalyticsEngine();
    }

    @Override
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("             CLASS-LEVEL STUDENT LIFESTYLE & ANALYTICS REPORT           \n");
        sb.append("========================================================================\n");
        sb.append(String.format(" Generated Date : %s\n", getFormattedTimestamp()));
        sb.append(String.format(" Total Students : %d\n", students.size()));
        sb.append("------------------------------------------------------------------------\n\n");

        // 1. Class Overall Averages
        sb.append("1. CLASS OVERALL AVERAGES\n");
        sb.append("------------------------------------------------------------------------\n");
        Map<String, Double> avgs = analyticsEngine.calculateClassAverages(students);
        for (Map.Entry<String, Double> e : avgs.entrySet()) {
            sb.append(String.format(" - %-30s : %.2f\n", e.getKey(), e.getValue()));
        }
        sb.append("\n");

        // 2. Performance Group Comparisons
        sb.append("2. LIFESTYLE COMPARISON BY PERFORMANCE CATEGORIES\n");
        sb.append("------------------------------------------------------------------------\n");
        List<GroupComparison> comparisons = analyticsEngine.calculateGroupComparisons(students);
        sb.append(String.format(" %-15s | %-6s | %-10s | %-10s | %-12s | %-10s | %-10s\n",
                "Category", "Count", "Avg Marks", "Avg Study", "Avg Attend %", "Avg Sleep", "Avg Screen"));
        sb.append("------------------------------------------------------------------------\n");
        for (GroupComparison gc : comparisons) {
            sb.append(String.format(" %-15s | %-6d | %-10.2f | %-10.2f | %-12.2f | %-10.2f | %-10.2f\n",
                    gc.getCategory().getDisplayName(),
                    gc.getStudentCount(),
                    gc.getAvgCurrentMarks(),
                    gc.getAvgStudyHours(),
                    gc.getAvgAttendance(),
                    gc.getAvgSleepHours(),
                    gc.getAvgScreenTime()));
        }
        sb.append("------------------------------------------------------------------------\n\n");

        // 3. Pairwise Key Correlations
        sb.append("3. PEARSON CORRELATION ANALYSIS\n");
        sb.append("------------------------------------------------------------------------\n");
        CorrelationResult r1 = analyticsEngine.calculateCorrelation(students, AnalyticsEngine.METRIC_STUDY_HOURS, AnalyticsEngine.METRIC_CURR_MARKS);
        CorrelationResult r2 = analyticsEngine.calculateCorrelation(students, AnalyticsEngine.METRIC_SLEEP_HOURS, AnalyticsEngine.METRIC_CURR_MARKS);
        CorrelationResult r3 = analyticsEngine.calculateCorrelation(students, AnalyticsEngine.METRIC_SCREEN_TIME, AnalyticsEngine.METRIC_CURR_MARKS);
        CorrelationResult r4 = analyticsEngine.calculateCorrelation(students, AnalyticsEngine.METRIC_ATTENDANCE, AnalyticsEngine.METRIC_CURR_MARKS);

        sb.append(String.format(" * %s vs %s: r = %.4f (%s)\n", r1.getVariableX(), r1.getVariableY(), r1.getPearsonR(), r1.getStrengthInterpretation()));
        sb.append(String.format(" * %s vs %s: r = %.4f (%s)\n", r2.getVariableX(), r2.getVariableY(), r2.getPearsonR(), r2.getStrengthInterpretation()));
        sb.append(String.format(" * %s vs %s: r = %.4f (%s)\n", r3.getVariableX(), r3.getVariableY(), r3.getPearsonR(), r3.getStrengthInterpretation()));
        sb.append(String.format(" * %s vs %s: r = %.4f (%s)\n", r4.getVariableX(), r4.getVariableY(), r4.getPearsonR(), r4.getStrengthInterpretation()));
        sb.append("\n");

        // 4. Detected Outliers / Anomalies
        sb.append("4. ANOMALY DETECTION SUMMARY (Z-Score Threshold >= 2.0)\n");
        sb.append("------------------------------------------------------------------------\n");
        List<AnomalyResult> anomalies = analyticsEngine.detectAnomalies(students, 2.0);
        sb.append(String.format(" Total Anomalous Students Flagged: %d\n", anomalies.size()));
        for (AnomalyResult ar : anomalies) {
            sb.append(String.format(" - [%s] %s (Max Z-Score: %.2f) -> %s\n",
                    ar.getStudent().getStudentId(),
                    ar.getStudent().getName(),
                    ar.getMaxZScore(),
                    ar.getExplanation()));
        }
        sb.append("\n");

        // 5. Mandatory Causation Caveat
        sb.append("5. MANDATORY SCIENTIFIC & ANALYTICAL CAVEAT\n");
        sb.append("------------------------------------------------------------------------\n");
        sb.append(" ");
        sb.append(CorrelationResult.MANDATORY_CAUSATION_CAVEAT);
        sb.append("\n========================================================================\n");

        return sb.toString();
    }
}
