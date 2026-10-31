package com.analytics.student.exception;

/**
 * Custom exception thrown when a mandatory field (e.g., student ID or name) is missing or blank.
 */
public class MissingFieldException extends InvalidStudentDataException {

    private final String fieldName;

    public MissingFieldException(String fieldName, int lineNumber) {
        super(String.format("Required field '%s' is missing or blank", fieldName), lineNumber);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
