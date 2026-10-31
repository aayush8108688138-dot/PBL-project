package com.analytics.student.exception;

/**
 * Custom exception thrown when a numerical metric exceeds realistic boundary constraints.
 */
public class OutOfRangeException extends InvalidStudentDataException {

    private final String fieldName;
    private final double invalidValue;
    private final double minAllowed;
    private final double maxAllowed;

    public OutOfRangeException(String fieldName, double invalidValue, double minAllowed, double maxAllowed, int lineNumber) {
        super(String.format("Field '%s' value %.2f is out of allowed range [%.2f, %.2f]",
                fieldName, invalidValue, minAllowed, maxAllowed), lineNumber);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.minAllowed = minAllowed;
        this.maxAllowed = maxAllowed;
    }

    public String getFieldName() {
        return fieldName;
    }

    public double getInvalidValue() {
        return invalidValue;
    }

    public double getMinAllowed() {
        return minAllowed;
    }

    public double getMaxAllowed() {
        return maxAllowed;
    }
}
