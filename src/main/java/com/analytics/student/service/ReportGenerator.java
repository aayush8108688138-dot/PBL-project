package com.analytics.student.service;

import com.analytics.student.model.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Abstract base class for analytical report generators.
 */
public abstract class ReportGenerator {

    protected final List<Student> students;
    protected final LocalDateTime generationTimestamp;

    public ReportGenerator(List<Student> students) {
        this.students = students != null ? students : List.of();
        this.generationTimestamp = LocalDateTime.now();
    }

    /**
     * Abstract method to generate plain-text or Markdown analytical report.
     *
     * @return String formatted report
     */
    public abstract String generateReport();

    /**
     * Exports the generated report string to a destination file on disk.
     *
     * @param destinationFile File object to save report
     * @throws IOException on file write error
     */
    public void exportToFile(File destinationFile) throws IOException {
        String reportContent = generateReport();
        try (PrintWriter writer = new PrintWriter(new FileWriter(destinationFile))) {
            writer.print(reportContent);
        }
    }

    protected String getFormattedTimestamp() {
        return generationTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
