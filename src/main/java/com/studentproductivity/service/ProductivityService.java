package com.studentproductivity.service;

import com.studentproductivity.config.AppConfig;
import com.studentproductivity.data.*;
import com.studentproductivity.database.DatabaseManager;
import com.studentproductivity.database.HistoryRecord;
import com.studentproductivity.database.LocalHistoryStore;
import com.studentproductivity.database.PredictionHistoryDao;
import com.studentproductivity.model.*;
import com.studentproductivity.prediction.InsightGenerator;
import com.studentproductivity.prediction.PredictionRequest;
import com.studentproductivity.prediction.PredictionResult;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Core Service orchestrating data loading, preprocessing, model training, evaluation,
 * prediction inference, insights, and history persistence.
 */
public class ProductivityService {

    private final AppConfig config;
    private final CsvLoader csvLoader;
    private final DataPreprocessor preprocessor;
    private final DatabaseManager dbManager;
    private final PredictionHistoryDao historyDao;

    private Dataset dataset;
    private CsvLoader.LoadingReport loadingReport;
    private DataPreprocessor.SplitResult splitResult;

    // Trained Models
    private MLModel activeModel;
    private final Map<String, MLModel> modelRegistry = new LinkedHashMap<>();
    private final Map<String, EvaluationMetrics> evaluationRegistry = new LinkedHashMap<>();

    private PredictionResult latestPrediction;

    public ProductivityService() {
        this.config = AppConfig.getInstance();
        this.csvLoader = new CsvLoader();
        this.preprocessor = new DataPreprocessor();
        this.dbManager = new DatabaseManager();
        this.historyDao = new PredictionHistoryDao(dbManager, new LocalHistoryStore());
    }

    /**
     * Loads the default dataset from project directory if available.
     */
    public boolean loadDefaultDataset() {
        File file = new File(config.getDefaultDatasetPath());
        if (file.exists()) {
            try {
                loadDataset(file);
                return true;
            } catch (Exception e) {
                System.err.println("Notice: Could not auto-load default dataset: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Load dataset from a specified CSV file.
     */
    public CsvLoader.LoadingReport loadDataset(File csvFile) throws IOException {
        CsvLoader.LoadResult result = csvLoader.loadCsv(csvFile);
        this.dataset = result.getDataset();
        this.loadingReport = result.getReport();
        return this.loadingReport;
    }

    /**
     * Preprocesses data and trains all candidate models, identifying the best performer.
     * @param seed Random seed for reproducible splitting and tree training (e.g. 42)
     */
    public Map<String, EvaluationMetrics> trainAndEvaluateModels(long seed) {
        if (dataset == null || dataset.isEmpty()) {
            throw new IllegalStateException("No dataset loaded to train models on.");
        }
        if (!dataset.hasTarget()) {
            throw new IllegalStateException("The loaded dataset does not contain target 'productivity_score'.");
        }

        // 1. Train/Test Split (80% train, 20% test)
        this.splitResult = preprocessor.splitTrainTest(dataset, 0.80, seed);

        modelRegistry.clear();
        evaluationRegistry.clear();

        // 2. Baseline Mean Model
        BaselineMeanModel baseline = new BaselineMeanModel();
        baseline.train(splitResult.getTrainX(), splitResult.getTrainY(), seed);
        EvaluationMetrics baseMetrics = ModelEvaluator.evaluate(baseline, splitResult.getTestX(), splitResult.getTestY());
        modelRegistry.put(baseline.getName(), baseline);
        evaluationRegistry.put(baseline.getName(), baseMetrics);

        // 3. Ridge Linear Regression Model
        LinearRegressionModel linear = new LinearRegressionModel(1.0);
        linear.train(splitResult.getTrainX(), splitResult.getTrainY(), seed);
        EvaluationMetrics linearMetrics = ModelEvaluator.evaluate(linear, splitResult.getTestX(), splitResult.getTestY());
        modelRegistry.put(linear.getName(), linear);
        evaluationRegistry.put(linear.getName(), linearMetrics);

        // 4. Decision Tree Regressor
        DecisionTreeRegressor tree = new DecisionTreeRegressor(10, 10, 5, null);
        tree.train(splitResult.getTrainX(), splitResult.getTrainY(), seed);
        EvaluationMetrics treeMetrics = ModelEvaluator.evaluate(tree, splitResult.getTestX(), splitResult.getTestY());
        modelRegistry.put(tree.getName(), tree);
        evaluationRegistry.put(tree.getName(), treeMetrics);

        // 5. Random Forest Regressor (Champion Model)
        RandomForestRegressor forest = new RandomForestRegressor(25, 12, 10, 5, 0.5);
        forest.train(splitResult.getTrainX(), splitResult.getTrainY(), seed);
        EvaluationMetrics forestMetrics = ModelEvaluator.evaluate(forest, splitResult.getTestX(), splitResult.getTestY());
        modelRegistry.put(forest.getName(), forest);
        evaluationRegistry.put(forest.getName(), forestMetrics);

        // Set Random Forest as active production model
        this.activeModel = forest;

        return Collections.unmodifiableMap(evaluationRegistry);
    }

    /**
     * Executes prediction inference on user input using the trained model.
     */
    public PredictionResult predict(PredictionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Prediction request cannot be null.");
        }
        List<String> errors = request.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation error: " + String.join("; ", errors));
        }

        if (activeModel == null || !activeModel.isTrained()) {
            throw new IllegalStateException("Machine learning model is not trained yet. Please load dataset and train the model first.");
        }

        // Convert request into a StudentRecord for uniform preprocessing
        StudentRecord dummyRecord = new StudentRecord(
                0, 0, request.getAge(), request.getGender(),
                request.getStudyHoursPerDay(), request.getSleepHours(),
                request.getPhoneUsageHours(), request.getSocialMediaHours(),
                request.getYoutubeHours(), request.getGamingHours(),
                request.getBreaksPerDay(), request.getCoffeeIntakeMg(),
                request.getExerciseMinutes(), request.getAssignmentsCompleted(),
                request.getAttendancePercentage(), request.getStressLevel(),
                request.getFocusScore(), request.getFinalGrade(), null
        );

        double[] rawFeatures = preprocessor.extractRawFeatures(dummyRecord);
        double[] scaledFeatures = preprocessor.transformRow(rawFeatures);

        double score;
        double lowerBound;
        double upperBound;
        double stdDev = 1.5;

        if (activeModel instanceof RandomForestRegressor) {
            RandomForestRegressor rf = (RandomForestRegressor) activeModel;
            RandomForestRegressor.PredictionInterval pi = rf.predictWithInterval(scaledFeatures);
            score = pi.getPrediction();
            lowerBound = pi.getLowerBound();
            upperBound = pi.getUpperBound();
            stdDev = pi.getStandardDeviation();
        } else {
            score = activeModel.predict(scaledFeatures);
            lowerBound = Math.max(0.0, score - 3.0);
            upperBound = Math.min(100.0, score + 3.0);
        }

        List<String> insights = InsightGenerator.generateInsights(request, dataset);

        PredictionResult result = new PredictionResult(
                score, lowerBound, upperBound, stdDev,
                activeModel.getName(), request, insights
        );

        this.latestPrediction = result;

        // Persist to history asynchronously or synchronously
        try {
            historyDao.savePrediction(result);
        } catch (Exception e) {
            System.err.println("Notice: Failed saving prediction to history: " + e.getMessage());
        }

        return result;
    }

    /**
     * Save trained model to disk.
     */
    public void saveModel(File file) throws IOException {
        if (activeModel == null) {
            throw new IllegalStateException("No active model to save.");
        }
        EvaluationMetrics metrics = evaluationRegistry.get(activeModel.getName());
        ModelPersistence.saveModel(file, activeModel, preprocessor, metrics);
    }

    /**
     * Load trained model from disk.
     */
    public void loadModel(File file) throws IOException, ClassNotFoundException {
        ModelPersistence.SavedModelBundle bundle = ModelPersistence.loadModel(file);
        this.activeModel = bundle.getModel();
        if (bundle.getMetrics() != null) {
            this.evaluationRegistry.put(activeModel.getName(), bundle.getMetrics());
        }
        this.modelRegistry.put(activeModel.getName(), activeModel);
    }

    // Accessors
    public Dataset getDataset() { return dataset; }
    public CsvLoader.LoadingReport getLoadingReport() { return loadingReport; }
    public boolean isDatasetLoaded() { return dataset != null && !dataset.isEmpty(); }
    public boolean isModelTrained() { return activeModel != null && activeModel.isTrained(); }
    public MLModel getActiveModel() { return activeModel; }
    public void setActiveModel(String modelName) {
        if (modelRegistry.containsKey(modelName)) {
            this.activeModel = modelRegistry.get(modelName);
        }
    }
    public Map<String, MLModel> getModelRegistry() { return Collections.unmodifiableMap(modelRegistry); }
    public Map<String, EvaluationMetrics> getEvaluationRegistry() { return Collections.unmodifiableMap(evaluationRegistry); }
    public PredictionResult getLatestPrediction() { return latestPrediction; }
    public DatabaseManager getDbManager() { return dbManager; }
    public PredictionHistoryDao getHistoryDao() { return historyDao; }
    public DataPreprocessor.SplitResult getSplitResult() { return splitResult; }
}
