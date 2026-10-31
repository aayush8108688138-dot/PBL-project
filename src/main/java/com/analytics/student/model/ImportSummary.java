package com.analytics.student.model;

import com.analytics.student.exception.InvalidStudentDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Summary container for CSV file import results, separating valid parsed records
 * from rejected malformed/invalid records.
 */
public class ImportSummary {

    private final List<Student> validStudents;
    private final List<InvalidStudentDataException> errors;
    private final int totalRowsProcessed;

    public ImportSummary(List<Student> validStudents, List<InvalidStudentDataException> errors, int totalRowsProcessed) {
        this.validStudents = validStudents != null ? validStudents : new ArrayList<>();
        this.errors = errors != null ? errors : new ArrayList<>();
        this.totalRowsProcessed = totalRowsProcessed;
    }

    public List<Student> getValidStudents() {
        return Collections.unmodifiableList(validStudents);
    }

    public List<InvalidStudentDataException> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public int getTotalRowsProcessed() {
        return totalRowsProcessed;
    }

    public int getSuccessCount() {
        return validStudents.size();
    }

    public int getErrorCount() {
        return errors.size();
    }

    @Override
    public String toString() {
        return String.format("ImportSummary[Total=%d, Valid=%d, Errors=%d]",
                totalRowsProcessed, getSuccessCount(), getErrorCount());
    }
}
