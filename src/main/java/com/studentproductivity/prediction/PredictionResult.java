package com.studentproductivity.prediction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of a model prediction inference, including estimate score, category,
 * uncertainty interval, and personalized educational insights.
 */
public class PredictionResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ProductivityCategory {
        LOW("Needs Attention", "#EF4444"),
        MODERATE("Moderate Productivity", "#F59E0B"),
        GOOD("Good Productivity", "#10B981"),
        EXCELLENT("High Productivity", "#3B82F6");

        private final String label;
        private final String hexColor;

        ProductivityCategory(String label, String hexColor) {
            this.label = label;
            this.hexColor = hexColor;
        }

        public String getLabel() { return label; }
        public String getHexColor() { return hexColor; }
    }

    private final double predictedScore;
    private final double lowerBound;
    private final double upperBound;
    private final double standardDeviation;
    private final ProductivityCategory category;
    private final String modelVersion;
    private final PredictionRequest request;
    private final List<String> personalizedInsights;
    private final long timestamp;

    public PredictionResult(double predictedScore, double lowerBound, double upperBound,
                            double standardDeviation, String modelVersion,
                            PredictionRequest request, List<String> personalizedInsights) {
        this.predictedScore = Math.max(0.0, Math.min(100.0, predictedScore));
        this.lowerBound = Math.max(0.0, Math.min(100.0, lowerBound));
        this.upperBound = Math.max(0.0, Math.min(100.0, upperBound));
        this.standardDeviation = standardDeviation;
        this.modelVersion = modelVersion;
        this.request = request;
        this.personalizedInsights = (personalizedInsights != null) ? new ArrayList<>(personalizedInsights) : new ArrayList<>();
        this.timestamp = System.currentTimeMillis();

        if (this.predictedScore < 40.0) {
            this.category = ProductivityCategory.LOW;
        } else if (this.predictedScore < 60.0) {
            this.category = ProductivityCategory.MODERATE;
        } else if (this.predictedScore < 80.0) {
            this.category = ProductivityCategory.GOOD;
        } else {
            this.category = ProductivityCategory.EXCELLENT;
        }
    }

    public double getPredictedScore() { return predictedScore; }
    public double getLowerBound() { return lowerBound; }
    public double getUpperBound() { return upperBound; }
    public double getStandardDeviation() { return standardDeviation; }
    public ProductivityCategory getCategory() { return category; }
    public String getModelVersion() { return modelVersion; }
    public PredictionRequest getRequest() { return request; }
    public List<String> getPersonalizedInsights() { return Collections.unmodifiableList(personalizedInsights); }
    public long getTimestamp() { return timestamp; }
}
