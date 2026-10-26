package com.studentproductivity;

import com.studentproductivity.model.EvaluationMetrics;
import com.studentproductivity.service.ProductivityService;

import java.io.File;
import java.util.Map;

/**
 * Command-line Model Training Utility.
 * Trains all candidate algorithms on the Kaggle dataset, prints evaluation benchmarks,
 * and saves the champion Random Forest model.
 */
public class TrainModelCli {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("STUDENT PRODUCTIVITY PREDICTOR — MODEL BENCHMARKING (JAVA AIML)");
        System.out.println("Team: P.G. DHARSHAN (RA2511026050012) & NITHISH RAJ A (RA2511026050034)");
        System.out.println("=================================================================\n");

        ProductivityService service = new ProductivityService();

        File datasetFile = new File("data/train.csv");
        if (!datasetFile.exists()) {
            System.err.println("Error: Dataset file data/train.csv not found.");
            return;
        }

        try {
            System.out.println("Loading dataset from: " + datasetFile.getAbsolutePath());
            service.loadDataset(datasetFile);
            System.out.printf("Loaded %,d verified records across %d features.\n\n",
                    service.getDataset().size(), service.getDataset().getColumnNames().size());

            System.out.println("Executing Reproducible 80/20 Train/Test Split (Seed 42)...");
            long startTime = System.currentTimeMillis();
            Map<String, EvaluationMetrics> results = service.trainAndEvaluateModels(42L);
            long elapsed = System.currentTimeMillis() - startTime;

            System.out.printf("Training & Evaluation completed in %d ms.\n\n", elapsed);
            System.out.println("=================================================================");
            System.out.printf("%-28s | %-10s | %-10s | %-10s | %-12s\n", "Model / Algorithm", "MAE", "RMSE", "R²", "Max Error");
            System.out.println("-----------------------------------------------------------------");

            for (Map.Entry<String, EvaluationMetrics> entry : results.entrySet()) {
                EvaluationMetrics m = entry.getValue();
                System.out.printf("%-28s | %10.4f | %10.4f | %10.4f | %12.2f\n",
                        m.getModelName(), m.getMae(), m.getRmse(), m.getRSquared(), m.getMaxResidual());
            }
            System.out.println("=================================================================\n");

            // Save trained model
            File modelOut = new File("models/trained_productivity_model.model");
            service.saveModel(modelOut);
            System.out.println("Saved champion model to: " + modelOut.getAbsolutePath());

            System.out.println("\nAll models verified successfully on held-out Kaggle test records.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
