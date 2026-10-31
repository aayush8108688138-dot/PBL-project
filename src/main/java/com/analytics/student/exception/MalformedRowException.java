package com.analytics.student.exception;

/**
 * Custom exception thrown when a CSV row is malformed (e.g., wrong column count or unparseable text format).
 */
public class MalformedRowException extends InvalidStudentDataException {

    private final String rawRowData;

    public MalformedRowException(String message, int lineNumber, String rawRowData) {
        super(message, lineNumber);
        this.rawRowData = rawRowData;
    }

    public String getRawRowData() {
        return rawRowData;
    }
}
