package com.studentproductivity.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Multiple Linear Regression with Ridge (L2) Regularization.
 * Solves (X^T * X + lambda * I) * beta = X^T * y using Gaussian elimination with partial pivoting.
 */
public class LinearRegressionModel implements MLModel {
    private static final long serialVersionUID = 1L;

    private double[] weights; // Includes weights for features and intercept as weights[weights.length - 1]
    private double intercept = 0.0;
    private double l2Lambda = 1.0;
    private boolean trained = false;

    public LinearRegressionModel() {
        this(1.0);
    }

    public LinearRegressionModel(double l2Lambda) {
        this.l2Lambda = Math.max(1e-4, l2Lambda);
    }

    @Override
    public void train(double[][] X, double[] y, long randomSeed) {
        if (X == null || y == null || X.length == 0 || y.length == 0) {
            throw new IllegalArgumentException("Training data cannot be empty.");
        }
        int n = X.length;
        int p = X[0].length;

        // Augment X with bias column of 1.0: dimension n x (p + 1)
        int dim = p + 1;
        double[][] XtX = new double[dim][dim];
        double[] Xty = new double[dim];

        for (int i = 0; i < n; i++) {
            double[] row = new double[dim];
            System.arraycopy(X[i], 0, row, 0, p);
            row[p] = 1.0; // intercept

            double yi = y[i];

            for (int j = 0; j < dim; j++) {
                double rj = row[j];
                Xty[j] += rj * yi;
                for (int k = j; k < dim; k++) {
                    XtX[j][k] += rj * row[k];
                }
            }
        }

        // Symmetrize XtX
        for (int j = 0; j < dim; j++) {
            for (int k = 0; k < j; k++) {
                XtX[j][k] = XtX[k][j];
            }
        }

        // Add Ridge L2 regularization (do not regularize intercept)
        for (int j = 0; j < p; j++) {
            XtX[j][j] += l2Lambda;
        }

        // Solve XtX * beta = Xty
        double[] beta = solveLinearSystem(XtX, Xty);

        weights = new double[p];
        System.arraycopy(beta, 0, weights, 0, p);
        intercept = beta[p];
        trained = true;
    }

    @Override
    public double predict(double[] x) {
        if (!trained || weights == null) {
            throw new IllegalStateException("LinearRegressionModel has not been trained.");
        }
        double sum = intercept;
        for (int j = 0; j < weights.length; j++) {
            sum += weights[j] * x[j];
        }
        // Clamp to sensible productivity bounds [0, 100]
        return Math.max(0.0, Math.min(100.0, sum));
    }

    @Override
    public String getName() {
        return "Ridge Linear Regression";
    }

    @Override
    public String getAlgorithmType() {
        return "Linear / Regularized OLS";
    }

    @Override
    public Map<String, Double> getFeatureImportances(String[] featureNames) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (!trained || weights == null || featureNames == null) {
            return Collections.emptyMap();
        }

        // Relative magnitude of normalized weights
        double sumAbs = 0.0;
        for (double w : weights) {
            sumAbs += Math.abs(w);
        }
        if (sumAbs < 1e-9) sumAbs = 1.0;

        for (int i = 0; i < Math.min(weights.length, featureNames.length); i++) {
            double importance = Math.abs(weights[i]) / sumAbs;
            map.put(featureNames[i], Math.round(importance * 10000.0) / 10000.0);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isTrained() {
        return trained;
    }

    public double[] getWeights() {
        return weights != null ? weights.clone() : new double[0];
    }

    public double getIntercept() {
        return intercept;
    }

    /**
     * Gaussian elimination with partial pivoting.
     */
    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n];
        double[] rhs = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            rhs[i] = b[i];
        }

        for (int p = 0; p < n; p++) {
            // Find pivot row
            int max = p;
            for (int i = p + 1; i < n; i++) {
                if (Math.abs(M[i][p]) > Math.abs(M[max][p])) {
                    max = i;
                }
            }

            // Swap rows
            double[] tempRow = M[p]; M[p] = M[max]; M[max] = tempRow;
            double tempRhs = rhs[p]; rhs[p] = rhs[max]; rhs[max] = tempRhs;

            if (Math.abs(M[p][p]) <= 1e-12) {
                M[p][p] = 1e-12; // Numerical stability damping
            }

            // Pivot within A and rhs
            for (int i = p + 1; i < n; i++) {
                double alpha = M[i][p] / M[p][p];
                rhs[i] -= alpha * rhs[p];
                for (int j = p; j < n; j++) {
                    M[i][j] -= alpha * M[p][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += M[i][j] * x[j];
            }
            x[i] = (rhs[i] - sum) / M[i][i];
        }
        return x;
    }
}
