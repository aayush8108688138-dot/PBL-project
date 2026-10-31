package com.analytics.student.model;

/**
 * Aggregated summary statistics for a performance category (HIGH, MEDIUM, LOW).
 */
public class GroupComparison {

    private final PerformanceCategory category;
    private final int studentCount;
    private final double avgStudyHours;
    private final double avgSleepHours;
    private final double avgAttendance;
    private final double avgAssignmentCompletion;
    private final double avgScreenTime;
    private final double avgExercise;
    private final double avgPreviousMarks;
    private final double avgCurrentMarks;

    public GroupComparison(PerformanceCategory category, int studentCount,
                           double avgStudyHours, double avgSleepHours,
                           double avgAttendance, double avgAssignmentCompletion,
                           double avgScreenTime, double avgExercise,
                           double avgPreviousMarks, double avgCurrentMarks) {
        this.category = category;
        this.studentCount = studentCount;
        this.avgStudyHours = avgStudyHours;
        this.avgSleepHours = avgSleepHours;
        this.avgAttendance = avgAttendance;
        this.avgAssignmentCompletion = avgAssignmentCompletion;
        this.avgScreenTime = avgScreenTime;
        this.avgExercise = avgExercise;
        this.avgPreviousMarks = avgPreviousMarks;
        this.avgCurrentMarks = avgCurrentMarks;
    }

    public PerformanceCategory getCategory() {
        return category;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public double getAvgStudyHours() {
        return avgStudyHours;
    }

    public double getAvgSleepHours() {
        return avgSleepHours;
    }

    public double getAvgAttendance() {
        return avgAttendance;
    }

    public double getAvgAssignmentCompletion() {
        return avgAssignmentCompletion;
    }

    public double getAvgScreenTime() {
        return avgScreenTime;
    }

    public double getAvgExercise() {
        return avgExercise;
    }

    public double getAvgPreviousMarks() {
        return avgPreviousMarks;
    }

    public double getAvgCurrentMarks() {
        return avgCurrentMarks;
    }

    @Override
    public String toString() {
        return String.format("GroupComparison[%s, count=%d, avgMarks=%.2f, avgStudy=%.2f, avgSleep=%.2f, avgAttendance=%.2f%%]",
                category, studentCount, avgCurrentMarks, avgStudyHours, avgSleepHours, avgAttendance);
    }
}
