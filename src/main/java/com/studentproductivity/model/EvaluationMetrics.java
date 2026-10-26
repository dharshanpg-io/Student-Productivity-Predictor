package com.studentproductivity.model;

import java.io.Serializable;

/**
 * Encapsulates standard regression evaluation metrics computed on held-out test data.
 */
public class EvaluationMetrics implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String modelName;
    private final int sampleCount;
    private final double mae;          // Mean Absolute Error
    private final double rmse;         // Root Mean Squared Error
    private final double rSquared;      // Coefficient of Determination (R^2)
    private final double meanTarget;
    private final double maxResidual;

    public EvaluationMetrics(String modelName, int sampleCount, double mae, double rmse,
                             double rSquared, double meanTarget, double maxResidual) {
        this.modelName = modelName;
        this.sampleCount = sampleCount;
        this.mae = mae;
        this.rmse = rmse;
        this.rSquared = rSquared;
        this.meanTarget = meanTarget;
        this.maxResidual = maxResidual;
    }

    public String getModelName() { return modelName; }
    public int getSampleCount() { return sampleCount; }
    public double getMae() { return mae; }
    public double getRmse() { return rmse; }
    public double getRSquared() { return rSquared; }
    public double getMeanTarget() { return meanTarget; }
    public double getMaxResidual() { return maxResidual; }

    @Override
    public String toString() {
        return String.format("%s: [Samples: %d, MAE: %.4f, RMSE: %.4f, R²: %.4f]",
                modelName, sampleCount, mae, rmse, rSquared);
    }
}
