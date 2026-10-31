package com.analytics.student.service;

import com.analytics.student.model.PerformanceCategory;
import com.analytics.student.model.Student;

/**
 * Functional interface for pluggable student performance classification logic.
 */
@FunctionalInterface
public interface Classifier {

    /**
     * Classifies a student into a PerformanceCategory based on custom rules.
     *
     * @param student Student object to evaluate
     * @return PerformanceCategory enum
     */
    PerformanceCategory classify(Student student);
}
