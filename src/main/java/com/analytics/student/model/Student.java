package com.analytics.student.model;

import java.util.Objects;

/**
 * Represents a student with lifestyle attributes and academic performance metrics.
 * Extends abstract Person base class.
 */
public class Student extends Person {

    private double studyHours;
    private double sleepHours;
    private double attendance;
    private double assignmentCompletion;
    private double screenTime;
    private double exerciseHours;
    private double previousMarks;
    private double currentMarks;
    private PerformanceCategory performanceCategory;
    private boolean anomaly;
    private String anomalyDetails;

    /**
     * Default Constructor.
     */
    public Student() {
        super();
        this.performanceCategory = PerformanceCategory.MEDIUM;
        this.anomaly = false;
        this.anomalyDetails = "";
    }

    /**
     * Full Parameterized Constructor.
     */
    public Student(String studentId, String name, double studyHours, double sleepHours,
                   double attendance, double assignmentCompletion, double screenTime,
                   double exerciseHours, double previousMarks, double currentMarks) {
        super(studentId, name);
        this.studyHours = studyHours;
        this.sleepHours = sleepHours;
        this.attendance = attendance;
        this.assignmentCompletion = assignmentCompletion;
        this.screenTime = screenTime;
        this.exerciseHours = exerciseHours;
        this.previousMarks = previousMarks;
        this.currentMarks = currentMarks;
        this.performanceCategory = PerformanceCategory.MEDIUM;
        this.anomaly = false;
        this.anomalyDetails = "";
    }

    @Override
    public String getDisplaySummary() {
        return String.format("[%s] %s - Marks: %.1f, Category: %s", studentId, name, currentMarks, performanceCategory);
    }

    // Getters and Setters

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStudyHours() {
        return studyHours;
    }

    public void setStudyHours(double studyHours) {
        this.studyHours = studyHours;
    }

    public double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public double getAttendance() {
        return attendance;
    }

    public void setAttendance(double attendance) {
        this.attendance = attendance;
    }

    public double getAssignmentCompletion() {
        return assignmentCompletion;
    }

    public void setAssignmentCompletion(double assignmentCompletion) {
        this.assignmentCompletion = assignmentCompletion;
    }

    public double getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(double screenTime) {
        this.screenTime = screenTime;
    }

    public double getExerciseHours() {
        return exerciseHours;
    }

    public void setExerciseHours(double exerciseHours) {
        this.exerciseHours = exerciseHours;
    }

    public double getPreviousMarks() {
        return previousMarks;
    }

    public void setPreviousMarks(double previousMarks) {
        this.previousMarks = previousMarks;
    }

    public double getCurrentMarks() {
        return currentMarks;
    }

    public void setCurrentMarks(double currentMarks) {
        this.currentMarks = currentMarks;
    }

    public PerformanceCategory getPerformanceCategory() {
        return performanceCategory;
    }

    public void setPerformanceCategory(PerformanceCategory performanceCategory) {
        this.performanceCategory = performanceCategory;
    }

    public boolean isAnomaly() {
        return anomaly;
    }

    public void setAnomaly(boolean anomaly) {
        this.anomaly = anomaly;
    }

    public String getAnomalyDetails() {
        return anomalyDetails;
    }

    public void setAnomalyDetails(String anomalyDetails) {
        this.anomalyDetails = anomalyDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("Student[ID='%s', Name='%s', CurrentMarks=%.2f, Category=%s, Anomaly=%b]",
                studentId, name, currentMarks, performanceCategory, anomaly);
    }
}
