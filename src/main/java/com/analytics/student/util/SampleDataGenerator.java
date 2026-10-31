package com.analytics.student.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Utility for generating realistic sample student lifestyle datasets including valid records and intentional invalid edge cases for testing.
 */
public class SampleDataGenerator {

    private static final String[] FIRST_NAMES = {"Aarav", "Ananya", "Rohan", "Priya", "Vikram", "Neha", "Aditya", "Sneha", "Karan", "Diya", "Rahul", "Isha", "Arjun", "Kavya", "Siddharth"};
    private static final String[] LAST_NAMES = {"Sharma", "Verma", "Patel", "Gupta", "Rao", "Nair", "Reddy", "Singh", "Joshi", "Mehta", "Chopra", "Kulkarni", "Deshmukh", "Iyer", "Bhat"};

    /**
     * Generates a sample CSV file containing student records.
     *
     * @param outputFile File path to save
     * @param recordCount total valid records to generate
     * @param includeInvalidRows whether to append intentional malformed/boundary rows for testing
     * @throws IOException on write error
     */
    public static void generateSampleCsv(File outputFile, int recordCount, boolean includeInvalidRows) throws IOException {
        Random rand = new Random(42); // Fixed seed for reproducible sample dataset

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            // Header
            writer.println("student_id,name,study_hours,sleep_hours,attendance,assignment_completion,screen_time,exercise,previous_marks,current_marks");

            for (int i = 1; i <= recordCount; i++) {
                String id = String.format("STU%03d", i);
                String name = FIRST_NAMES[rand.nextInt(FIRST_NAMES.length)] + " " + LAST_NAMES[rand.nextInt(LAST_NAMES.length)];

                // Generate realistic correlated metrics
                double studyHours = Math.round((2.0 + rand.nextDouble() * 8.0) * 10.0) / 10.0; // 2.0 to 10.0
                double sleepHours = Math.round((5.0 + rand.nextDouble() * 4.0) * 10.0) / 10.0; // 5.0 to 9.0
                double attendance = Math.round((55.0 + rand.nextDouble() * 43.0) * 10.0) / 10.0; // 55 to 98%
                double assignmentCompletion = Math.round((attendance * 0.8 + rand.nextDouble() * 15.0) * 10.0) / 10.0;
                if (assignmentCompletion > 100.0) assignmentCompletion = 100.0;

                double screenTime = Math.round((1.5 + rand.nextDouble() * 6.5) * 10.0) / 10.0;
                double exercise = Math.round((0.5 + rand.nextDouble() * 2.5) * 10.0) / 10.0;

                double previousMarks = Math.round((45.0 + rand.nextDouble() * 50.0) * 10.0) / 10.0;

                // Current marks heavily correlated with study hours and attendance
                double noise = (rand.nextDouble() - 0.5) * 15.0;
                double currentMarks = Math.round((previousMarks * 0.4 + studyHours * 5.0 + attendance * 0.3 + noise) * 10.0) / 10.0;
                if (currentMarks > 100.0) currentMarks = 100.0;
                if (currentMarks < 30.0) currentMarks = 30.0;

                // Create a few synthetic anomalies (e.g. high study hours but low marks, or 12h screen time)
                if (i == 15) {
                    studyHours = 12.0; currentMarks = 35.0; // Anomaly: High study, low marks
                } else if (i == 28) {
                    screenTime = 14.0; attendance = 32.0; currentMarks = 40.0; // Anomaly: Extreme screen time
                }

                writer.printf("%s,%s,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f%n",
                        id, name, studyHours, sleepHours, attendance, assignmentCompletion, screenTime, exercise, previousMarks, currentMarks);
            }

            if (includeInvalidRows) {
                // Invalid Edge cases for error handling validation tests
                writer.println("STU999,Invalid Sleep Student,5.0,28.0,85.0,90.0,3.0,1.0,75.0,80.0"); // Out of range sleep (28h)
                writer.println("STU998,,6.0,7.0,80.0,85.0,2.0,1.5,70.0,72.0"); // Missing Name
                writer.println("STU997,Malformed Row,4.0,6.0,90.0"); // Malformed line (too few columns)
                writer.println("STU996,Invalid Attendance,8.0,7.0,120.0,90.0,2.0,1.0,80.0,85.0"); // Out of range attendance (120%)
            }
        }
    }
}
