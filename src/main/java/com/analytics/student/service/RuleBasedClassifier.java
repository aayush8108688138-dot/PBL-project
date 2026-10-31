package com.analytics.student.service;

import com.analytics.student.model.PerformanceCategory;
import com.analytics.student.model.Student;

/**
 * Rule-based classifier implementation with configurable academic mark thresholds.
 */
public class RuleBasedClassifier implements Classifier {

    private double highThreshold;
    private double lowThreshold;

    /**
     * Default constructor (High >= 75.0, Low < 50.0).
     */
    public RuleBasedClassifier() {
        this(75.0, 50.0);
    }

    /**
     * Parameterized constructor for custom classification thresholds.
     *
     * @param highThreshold minimum current marks for HIGH performer category
     * @param lowThreshold upper bound marks for LOW performer category
     */
    public RuleBasedClassifier(double highThreshold, double lowThreshold) {
        this.highThreshold = highThreshold;
        this.lowThreshold = lowThreshold;
    }

    @Override
    public PerformanceCategory classify(Student student) {
        if (student == null) {
            return PerformanceCategory.MEDIUM;
        }

        double marks = student.getCurrentMarks();
        if (marks >= highThreshold) {
            return PerformanceCategory.HIGH;
        } else if (marks < lowThreshold) {
            return PerformanceCategory.LOW;
        } else {
            return PerformanceCategory.MEDIUM;
        }
    }

    public double getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(double highThreshold) {
        this.highThreshold = highThreshold;
    }

    public double getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(double lowThreshold) {
        this.lowThreshold = lowThreshold;
    }
}
