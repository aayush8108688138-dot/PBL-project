package com.analytics.student.model;

/**
 * Data transfer object encapsulating Pearson correlation calculation results
 * between two student lifestyle/academic metrics, including mandatory non-causation disclaimer.
 */
public class CorrelationResult {

    public static final String MANDATORY_CAUSATION_CAVEAT =
            "CAVEAT: Correlation does not imply causation. A statistically strong correlation between variables " +
            "(e.g., study hours and current marks) indicates co-occurrence or association within this dataset, " +
            "not a direct causal connection. Other confounding variables (e.g., prior knowledge, teaching quality, self-discipline) " +
            "may influence both metrics.";

    private final String variableX;
    private final String variableY;
    private final double pearsonR;
    private final int sampleSize;
    private final String strengthInterpretation;

    public CorrelationResult(String variableX, String variableY, double pearsonR, int sampleSize) {
        this.variableX = variableX;
        this.variableY = variableY;
        this.pearsonR = pearsonR;
        this.sampleSize = sampleSize;
        this.strengthInterpretation = interpretStrength(pearsonR);
    }

    private String interpretStrength(double r) {
        double absR = Math.abs(r);
        String direction = r >= 0 ? "positive" : "negative";
        if (absR >= 0.8) {
            return "Very Strong " + direction + " linear correlation";
        } else if (absR >= 0.6) {
            return "Strong " + direction + " linear correlation";
        } else if (absR >= 0.4) {
            return "Moderate " + direction + " linear correlation";
        } else if (absR >= 0.2) {
            return "Weak " + direction + " linear correlation";
        } else {
            return "Very weak or no linear correlation";
        }
    }

    public String getVariableX() {
        return variableX;
    }

    public String getVariableY() {
        return variableY;
    }

    public double getPearsonR() {
        return pearsonR;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public String getStrengthInterpretation() {
        return strengthInterpretation;
    }

    public String getCausationCaveat() {
        return MANDATORY_CAUSATION_CAVEAT;
    }

    @Override
    public String toString() {
        return String.format("Correlation[%s vs %s]: r = %.4f (%s, n=%d)\n%s",
                variableX, variableY, pearsonR, strengthInterpretation, sampleSize, MANDATORY_CAUSATION_CAVEAT);
    }
}
