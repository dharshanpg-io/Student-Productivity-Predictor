package com.studentproductivity.model;

/**
 * Utility for computing standard mathematical regression evaluation metrics.
 */
public class ModelEvaluator {

    public static EvaluationMetrics evaluate(MLModel model, double[][] testX, double[] testY) {
        if (testX == null || testY == null || testX.length == 0 || testY.length == 0) {
            throw new IllegalArgumentException("Test set cannot be empty.");
        }
        if (testX.length != testY.length) {
            throw new IllegalArgumentException("Features and targets length mismatch: " + testX.length + " vs " + testY.length);
        }

        int n = testY.length;
        double sumAbsErr = 0.0;
        double sumSqErr = 0.0;
        double sumY = 0.0;
        double maxResidual = 0.0;

        for (int i = 0; i < n; i++) {
            double actual = testY[i];
            double pred = model.predict(testX[i]);

            double err = actual - pred;
            double absErr = Math.abs(err);
            sumAbsErr += absErr;
            sumSqErr += err * err;
            sumY += actual;

            if (absErr > maxResidual) {
                maxResidual = absErr;
            }
        }

        double mae = sumAbsErr / n;
        double mse = sumSqErr / n;
        double rmse = Math.sqrt(mse);
        double meanY = sumY / n;

        // Calculate Total Sum of Squares (TSS) for R^2
        double tss = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = testY[i] - meanY;
            tss += diff * diff;
        }

        double rSquared;
        if (tss < 1e-9) {
            rSquared = 1.0;
        } else {
            rSquared = 1.0 - (sumSqErr / tss);
        }

        return new EvaluationMetrics(
                model.getName(),
                n,
                round(mae, 4),
                round(rmse, 4),
                round(rSquared, 4),
                round(meanY, 4),
                round(maxResidual, 4)
        );
    }

    private static double round(double val, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(val * factor) / factor;
    }
}
