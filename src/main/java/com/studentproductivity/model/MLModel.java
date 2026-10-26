package com.studentproductivity.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Common interface for all Machine Learning Regression models in the application.
 */
public interface MLModel extends Serializable {

    /**
     * Train the regression model on the given feature matrix X and target vector y.
     */
    void train(double[][] X, double[] y, long randomSeed);

    /**
     * Predict a numerical target value for an individual feature vector x.
     */
    double predict(double[] x);

    /**
     * Get the descriptive name of the model algorithm.
     */
    String getName();

    /**
     * Get the algorithm category/family.
     */
    String getAlgorithmType();

    /**
     * Get feature importance mapping (or coefficients for linear models).
     */
    Map<String, Double> getFeatureImportances(String[] featureNames);

    /**
     * Checks if the model has been trained.
     */
    boolean isTrained();
}
