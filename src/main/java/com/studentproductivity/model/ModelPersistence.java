package com.studentproductivity.model;

import com.studentproductivity.data.DataPreprocessor;

import java.io.*;

/**
 * Handles binary serialization and deserialization of trained models and preprocessing states.
 */
public class ModelPersistence {

    public static class SavedModelBundle implements Serializable {
        private static final long serialVersionUID = 1L;

        private final MLModel model;
        private final DataPreprocessor preprocessor;
        private final EvaluationMetrics metrics;
        private final long savedTimestamp;

        public SavedModelBundle(MLModel model, DataPreprocessor preprocessor, EvaluationMetrics metrics) {
            this.model = model;
            this.preprocessor = preprocessor;
            this.metrics = metrics;
            this.savedTimestamp = System.currentTimeMillis();
        }

        public MLModel getModel() { return model; }
        public DataPreprocessor getPreprocessor() { return preprocessor; }
        public EvaluationMetrics getMetrics() { return metrics; }
        public long getSavedTimestamp() { return savedTimestamp; }
    }

    public static void saveModel(File file, MLModel model, DataPreprocessor preprocessor, EvaluationMetrics metrics) throws IOException {
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            SavedModelBundle bundle = new SavedModelBundle(model, preprocessor, metrics);
            oos.writeObject(bundle);
        }
    }

    public static SavedModelBundle loadModel(File file) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("Model file does not exist: " + file.getAbsolutePath());
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SavedModelBundle) ois.readObject();
        }
    }
}
