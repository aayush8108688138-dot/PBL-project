package com.analytics.student.util;

import com.analytics.student.model.CorrelationResult;
import com.analytics.student.model.Student;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Utility for computing Pearson Correlation Coefficient using Java Streams API.
 */
public class CorrelationCalculator {

    /**
     * Calculates Pearson correlation coefficient between two numeric fields of Student objects.
     *
     * @param students list of Student records
     * @param varXLabel human-readable label for X variable
     * @param extractorX function extracting X metric
     * @param varYLabel human-readable label for Y variable
     * @param extractorY function extracting Y metric
     * @return CorrelationResult container
     */
    public static CorrelationResult calculatePearson(List<Student> students,
                                                       String varXLabel, ToDoubleFunction<Student> extractorX,
                                                       String varYLabel, ToDoubleFunction<Student> extractorY) {
        if (students == null || students.size() < 2) {
            return new CorrelationResult(varXLabel, varYLabel, 0.0, students != null ? students.size() : 0);
        }

        int n = students.size();

        // Calculate means using Java Streams
        double meanX = students.stream().mapToDouble(extractorX).average().orElse(0.0);
        double meanY = students.stream().mapToDouble(extractorY).average().orElse(0.0);

        double numerator = 0.0;
        double sumSquareX = 0.0;
        double sumSquareY = 0.0;

        for (Student s : students) {
            double diffX = extractorX.applyAsDouble(s) - meanX;
            double diffY = extractorY.applyAsDouble(s) - meanY;

            numerator += diffX * diffY;
            sumSquareX += diffX * diffX;
            sumSquareY += diffY * diffY;
        }

        double denominator = Math.sqrt(sumSquareX * sumSquareY);
        double pearsonR = (denominator == 0.0) ? 0.0 : numerator / denominator;

        return new CorrelationResult(varXLabel, varYLabel, pearsonR, n);
    }
}
