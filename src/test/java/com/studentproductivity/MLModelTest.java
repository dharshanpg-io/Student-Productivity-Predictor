package com.studentproductivity;

import com.studentproductivity.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MLModelTest {

    @Test
    void testBaselineModelPredictsMean() {
        BaselineMeanModel baseline = new BaselineMeanModel();
        double[][] X = {{1.0}, {2.0}, {3.0}};
        double[] y = {50.0, 60.0, 70.0}; // Mean = 60.0

        baseline.train(X, y, 42L);
        assertTrue(baseline.isTrained());
        assertEquals(60.0, baseline.predict(new double[]{10.0}), 1e-6);
    }

    @Test
    void testLinearRegressionModelFit() {
        LinearRegressionModel model = new LinearRegressionModel(0.01);
        // Simple synthetic linear relationship: y = 2 * x + 10
        double[][] X = new double[100][1];
        double[] y = new double[100];
        for (int i = 0; i < 100; i++) {
            X[i][0] = i * 0.5;
            y[i] = 2.0 * X[i][0] + 10.0;
        }

        model.train(X, y, 42L);
        assertTrue(model.isTrained());

        // Test prediction at x = 15 -> y should be approx 40
        double pred = model.predict(new double[]{15.0});
        assertEquals(40.0, pred, 1.0);
    }

    @Test
    void testRandomForestModelTrainAndPredict() {
        RandomForestRegressor forest = new RandomForestRegressor(10, 8, 5, 2, 0.8);
        double[][] X = new double[80][3];
        double[] y = new double[80];
        for (int i = 0; i < 80; i++) {
            X[i][0] = i * 0.1;
            X[i][1] = 5.0;
            X[i][2] = (i % 2 == 0) ? 1.0 : 0.0;
            y[i] = 30.0 + 3.0 * X[i][0];
        }

        forest.train(X, y, 42L);
        assertTrue(forest.isTrained());

        double pred = forest.predict(new double[]{4.0, 5.0, 1.0});
        assertTrue(pred >= 0.0 && pred <= 100.0);
    }

    @Test
    void testModelEvaluatorMetrics() {
        // Dummy model that predicts actual + 2
        MLModel dummy = new MLModel() {
            @Override public void train(double[][] X, double[] y, long s) {}
            @Override public double predict(double[] x) { return x[0] + 2.0; }
            @Override public String getName() { return "Dummy"; }
            @Override public String getAlgorithmType() { return "Test"; }
            @Override public java.util.Map<String, Double> getFeatureImportances(String[] f) { return java.util.Collections.emptyMap(); }
            @Override public boolean isTrained() { return true; }
        };

        double[][] testX = {{10.0}, {20.0}, {30.0}};
        double[] testY = {10.0, 20.0, 30.0}; // Predictions will be 12, 22, 32 -> Error exactly +2

        EvaluationMetrics m = ModelEvaluator.evaluate(dummy, testX, testY);
        assertEquals(2.0, m.getMae(), 1e-4);
        assertEquals(2.0, m.getRmse(), 1e-4);
    }
}
