package com.analytics.student.exception;

/**
 * Base custom exception for all student data validation and parsing errors.
 */
public class InvalidStudentDataException extends Exception {

    private final int lineNumber;

    public InvalidStudentDataException(String message) {
        super(message);
        this.lineNumber = -1;
    }

    public InvalidStudentDataException(String message, int lineNumber) {
        super(String.format("Line %d: %s", lineNumber, message));
        this.lineNumber = lineNumber;
    }

    public InvalidStudentDataException(String message, Throwable cause, int lineNumber) {
        super(String.format("Line %d: %s", lineNumber, message), cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
