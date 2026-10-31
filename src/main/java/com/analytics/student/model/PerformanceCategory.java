package com.analytics.student.model;

/**
 * Performance category classification for students based on academic marks.
 */
public enum PerformanceCategory {
    HIGH("High Performer", "Marks >= 75%"),
    MEDIUM("Medium Performer", "50% <= Marks < 75%"),
    LOW("Low Performer", "Marks < 50%");

    private final String displayName;
    private final String criteriaDescription;

    PerformanceCategory(String displayName, String criteriaDescription) {
        this.displayName = displayName;
        this.criteriaDescription = criteriaDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCriteriaDescription() {
        return criteriaDescription;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
