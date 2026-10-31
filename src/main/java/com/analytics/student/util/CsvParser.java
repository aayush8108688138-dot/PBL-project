package com.analytics.student.util;

import com.analytics.student.exception.*;
import com.analytics.student.model.ImportSummary;
import com.analytics.student.model.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing and validating student records from CSV data streams.
 */
public class CsvParser {

    private static final int EXPECTED_COLUMNS = 10;

    /**
     * Parses a CSV file into an ImportSummary containing valid Student records and caught validation exceptions.
     *
     * @param file CSV File to parse
     * @return ImportSummary containing results
     * @throws IOException if file reading fails
     */
    public static ImportSummary parseCsvFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return parseCsvStream(is);
        }
    }

    /**
     * Parses CSV data from an InputStream.
     *
     * @param inputStream InputStream containing CSV text data
     * @return ImportSummary with valid records and errors
     * @throws IOException if stream reading fails
     */
    public static ImportSummary parseCsvStream(InputStream inputStream) throws IOException {
        List<Student> validStudents = new ArrayList<>();
        List<InvalidStudentDataException> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmedLine = line.trim();

                // Skip blank lines
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                // Check for header row (contains 'student_id' or 'student id')
                if (lineNumber == 1 && isHeaderRow(trimmedLine)) {
                    continue;
                }

                try {
                    Student student = parseRow(trimmedLine, lineNumber);
                    validStudents.add(student);
                } catch (InvalidStudentDataException ex) {
                    errors.add(ex);
                }
            }

            return new ImportSummary(validStudents, errors, lineNumber);
        }
    }

    private static boolean isHeaderRow(String line) {
        String lower = line.toLowerCase();
        return lower.contains("student_id") || lower.contains("studentid") || lower.contains("study_hours");
    }

    /**
     * Parses a single row of CSV string into a validated Student object.
     *
     * @param row raw CSV line
     * @param lineNumber line number for error reporting
     * @return Student object
     * @throws InvalidStudentDataException if parsing or validation fails
     */
    public static Student parseRow(String row, int lineNumber) throws InvalidStudentDataException {
        // Split by comma ignoring commas inside quotes
        String[] tokens = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        if (tokens.length < EXPECTED_COLUMNS) {
            throw new MalformedRowException(
                    String.format("Expected at least %d columns, but found %d columns", EXPECTED_COLUMNS, tokens.length),
                    lineNumber, row);
        }

        String studentId = cleanToken(tokens[0]);
        String name = cleanToken(tokens[1]);

        if (studentId.isEmpty()) {
            throw new MissingFieldException("student_id", lineNumber);
        }
        if (name.isEmpty()) {
            throw new MissingFieldException("name", lineNumber);
        }

        double studyHours = parseDoubleField("study_hours", tokens[2], 0.0, 24.0, lineNumber);
        double sleepHours = parseDoubleField("sleep_hours", tokens[3], 0.0, 24.0, lineNumber);
        double attendance = parseDoubleField("attendance", tokens[4], 0.0, 100.0, lineNumber);
        double assignmentCompletion = parseDoubleField("assignment_completion", tokens[5], 0.0, 100.0, lineNumber);
        double screenTime = parseDoubleField("screen_time", tokens[6], 0.0, 24.0, lineNumber);
        double exerciseHours = parseDoubleField("exercise", tokens[7], 0.0, 24.0, lineNumber);
        double previousMarks = parseDoubleField("previous_marks", tokens[8], 0.0, 100.0, lineNumber);
        double currentMarks = parseDoubleField("current_marks", tokens[9], 0.0, 100.0, lineNumber);

        return new Student(studentId, name, studyHours, sleepHours, attendance,
                assignmentCompletion, screenTime, exerciseHours, previousMarks, currentMarks);
    }

    private static String cleanToken(String token) {
        if (token == null) return "";
        String trimmed = token.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private static double parseDoubleField(String fieldName, String rawToken, double min, double max, int lineNumber)
            throws InvalidStudentDataException {
        String cleaned = cleanToken(rawToken);
        if (cleaned.isEmpty()) {
            throw new MissingFieldException(fieldName, lineNumber);
        }

        double val;
        try {
            val = Double.parseDouble(cleaned);
        } catch (NumberFormatException ex) {
            throw new MalformedRowException(
                    String.format("Field '%s' contains non-numeric value '%s'", fieldName, cleaned),
                    lineNumber, rawToken);
        }

        if (val < min || val > max) {
            throw new OutOfRangeException(fieldName, val, min, max, lineNumber);
        }

        return val;
    }
}
