package com.studentproductivity.data;

import java.io.Serializable;
import java.util.*;

/**
 * Data Preprocessor for preparing feature matrices and target vectors.
 * Handles one-hot encoding, feature normalization (StandardScaler / MinMaxScaler),
 * and reproducible train/test splits without data leakage.
 */
public class DataPreprocessor implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] FEATURE_NAMES = {
            "age",
            "gender_Male",
            "gender_Female",
            "gender_Other",
            "study_hours_per_day",
            "sleep_hours",
            "phone_usage_hours",
            "social_media_hours",
            "youtube_hours",
            "gaming_hours",
            "breaks_per_day",
            "coffee_intake_mg",
            "exercise_minutes",
            "assignments_completed",
            "attendance_percentage",
            "stress_level",
            "focus_score",
            "final_grade"
    };

    private double[] featureMeans;
    private double[] featureStdDevs;
    private boolean isFitted = false;

    public static class SplitResult {
        private final double[][] trainX;
        private final double[] trainY;
        private final double[][] testX;
        private final double[] testY;
        private final List<StudentRecord> trainRecords;
        private final List<StudentRecord> testRecords;

        public SplitResult(double[][] trainX, double[] trainY, double[][] testX, double[] testY,
                           List<StudentRecord> trainRecords, List<StudentRecord> testRecords) {
            this.trainX = trainX;
            this.trainY = trainY;
            this.testX = testX;
            this.testY = testY;
            this.trainRecords = trainRecords;
            this.testRecords = testRecords;
        }

        public double[][] getTrainX() { return trainX; }
        public double[] getTrainY() { return trainY; }
        public double[][] getTestX() { return testX; }
        public double[] getTestY() { return testY; }
        public List<StudentRecord> getTrainRecords() { return trainRecords; }
        public List<StudentRecord> getTestRecords() { return testRecords; }
    }

    /**
     * Extracts raw numerical feature vector from a student record.
     */
    public double[] extractRawFeatures(StudentRecord r) {
        double[] x = new double[FEATURE_NAMES.length];
        x[0] = r.getAge();

        // One-hot encode gender
        String g = (r.getGender() != null) ? r.getGender().trim().toLowerCase() : "other";
        x[1] = g.equals("male") ? 1.0 : 0.0;
        x[2] = g.equals("female") ? 1.0 : 0.0;
        x[3] = (!g.equals("male") && !g.equals("female")) ? 1.0 : 0.0;

        x[4] = r.getStudyHoursPerDay();
        x[5] = r.getSleepHours();
        x[6] = r.getPhoneUsageHours();
        x[7] = r.getSocialMediaHours();
        x[8] = r.getYoutubeHours();
        x[9] = r.getGamingHours();
        x[10] = r.getBreaksPerDay();
        x[11] = r.getCoffeeIntakeMg();
        x[12] = r.getExerciseMinutes();
        x[13] = r.getAssignmentsCompleted();
        x[14] = r.getAttendancePercentage();
        x[15] = r.getStressLevel();
        x[16] = r.getFocusScore();
        x[17] = r.getFinalGrade();
        return x;
    }

    /**
     * Fit scaler parameters (mean and std dev) strictly on training data to prevent target leakage.
     */
    public void fitScaler(double[][] trainX) {
        int n = trainX.length;
        int p = FEATURE_NAMES.length;
        featureMeans = new double[p];
        featureStdDevs = new double[p];

        for (int j = 0; j < p; j++) {
            double sum = 0.0;
            for (int i = 0; i < n; i++) {
                sum += trainX[i][j];
            }
            featureMeans[j] = sum / n;

            double sumSq = 0.0;
            for (int i = 0; i < n; i++) {
                double diff = trainX[i][j] - featureMeans[j];
                sumSq += diff * diff;
            }
            double variance = sumSq / Math.max(1, n - 1);
            double std = Math.sqrt(variance);
            featureStdDevs[j] = (std < 1e-7) ? 1.0 : std; // Avoid division by zero
        }
        isFitted = true;
    }

    /**
     * Transform feature matrix using fitted parameters (z-score standardization).
     */
    public double[][] transform(double[][] X) {
        if (!isFitted) {
            throw new IllegalStateException("DataPreprocessor has not been fitted yet.");
        }
        double[][] scaled = new double[X.length][FEATURE_NAMES.length];
        for (int i = 0; i < X.length; i++) {
            scaled[i] = transformRow(X[i]);
        }
        return scaled;
    }

    /**
     * Transform a single feature row.
     */
    public double[] transformRow(double[] row) {
        if (!isFitted) {
            return row.clone();
        }
        double[] scaled = new double[row.length];
        for (int j = 0; j < row.length; j++) {
            scaled[j] = (row[j] - featureMeans[j]) / featureStdDevs[j];
        }
        return scaled;
    }

    /**
     * Splits a dataset into training and testing sets reproducibly.
     * @param dataset The loaded dataset
     * @param trainRatio Proportion for training (e.g. 0.8)
     * @param randomSeed Fixed seed for deterministic splitting
     */
    public SplitResult splitTrainTest(Dataset dataset, double trainRatio, long randomSeed) {
        List<StudentRecord> allRecords = new ArrayList<>(dataset.getRecords());
        // Shuffle reproducibly
        Collections.shuffle(allRecords, new Random(randomSeed));

        int trainSize = (int) Math.round(allRecords.size() * trainRatio);
        List<StudentRecord> trainList = allRecords.subList(0, trainSize);
        List<StudentRecord> testList = allRecords.subList(trainSize, allRecords.size());

        double[][] rawTrainX = new double[trainList.size()][FEATURE_NAMES.length];
        double[] trainY = new double[trainList.size()];
        for (int i = 0; i < trainList.size(); i++) {
            rawTrainX[i] = extractRawFeatures(trainList.get(i));
            trainY[i] = trainList.get(i).getProductivityScore();
        }

        double[][] rawTestX = new double[testList.size()][FEATURE_NAMES.length];
        double[] testY = new double[testList.size()];
        for (int i = 0; i < testList.size(); i++) {
            rawTestX[i] = extractRawFeatures(testList.get(i));
            testY[i] = testList.get(i).getProductivityScore();
        }

        // Fit scaler ONLY on raw training features
        fitScaler(rawTrainX);

        // Scale both train and test
        double[][] scaledTrainX = transform(rawTrainX);
        double[][] scaledTestX = transform(rawTestX);

        return new SplitResult(scaledTrainX, trainY, scaledTestX, testY, trainList, testList);
    }

    public boolean isFitted() { return isFitted; }
    public double[] getFeatureMeans() { return featureMeans; }
    public double[] getFeatureStdDevs() { return featureStdDevs; }
}
