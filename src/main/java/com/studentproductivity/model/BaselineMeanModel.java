package com.studentproductivity.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Baseline regression model that always predicts the sample mean of the training target.
 * Serves as a reference benchmark for measuring actual predictive lift.
 */
public class BaselineMeanModel implements MLModel {
    private static final long serialVersionUID = 1L;

    private double meanProductivity = 50.0;
    private boolean trained = false;

    @Override
    public void train(double[][] X, double[] y, long randomSeed) {
        if (y == null || y.length == 0) {
            throw new IllegalArgumentException("Target vector y cannot be empty.");
        }
        double sum = 0.0;
        for (double v : y) {
            sum += v;
        }
        this.meanProductivity = sum / y.length;
        this.trained = true;
    }

    @Override
    public double predict(double[] x) {
        return meanProductivity;
    }

    @Override
    public String getName() {
        return "Baseline (Mean Predictor)";
    }

    @Override
    public String getAlgorithmType() {
        return "Baseline Reference";
    }

    @Override
    public Map<String, Double> getFeatureImportances(String[] featureNames) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (featureNames != null) {
            for (String feat : featureNames) {
                map.put(feat, 0.0);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isTrained() {
        return trained;
    }

    public double getMeanProductivity() {
        return meanProductivity;
    }
}
