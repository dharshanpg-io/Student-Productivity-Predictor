package com.studentproductivity.model;

import java.io.Serializable;
import java.util.*;

/**
 * Regression Decision Tree using variance reduction splitting.
 */
public class DecisionTreeRegressor implements MLModel {
    private static final long serialVersionUID = 1L;

    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;

        boolean isLeaf;
        double prediction;
        int featureIndex = -1;
        double threshold;
        Node left;
        Node right;
        double impurityReduction;
    }

    private final int maxDepth;
    private final int minSamplesSplit;
    private final int minSamplesLeaf;
    private final Integer maxFeatures; // If null, consider all features

    private Node root;
    private double[] featureImportances;
    private boolean trained = false;

    public DecisionTreeRegressor() {
        this(12, 10, 5, null);
    }

    public DecisionTreeRegressor(int maxDepth, int minSamplesSplit, int minSamplesLeaf, Integer maxFeatures) {
        this.maxDepth = maxDepth;
        this.minSamplesSplit = minSamplesSplit;
        this.minSamplesLeaf = minSamplesLeaf;
        this.maxFeatures = maxFeatures;
    }

    @Override
    public void train(double[][] X, double[] y, long randomSeed) {
        if (X == null || y == null || X.length == 0 || y.length == 0) {
            throw new IllegalArgumentException("Training data cannot be empty.");
        }
        int n = X.length;
        int p = X[0].length;
        featureImportances = new double[p];

        int[] sampleIndices = new int[n];
        for (int i = 0; i < n; i++) sampleIndices[i] = i;

        Random rng = new Random(randomSeed);
        root = buildTree(X, y, sampleIndices, 0, rng);
        normalizeImportances();
        trained = true;
    }

    private Node buildTree(double[][] X, double[] y, int[] indices, int currentDepth, Random rng) {
        Node node = new Node();
        double nodeMean = computeMean(y, indices);
        node.prediction = nodeMean;

        if (indices.length < minSamplesSplit || currentDepth >= maxDepth) {
            node.isLeaf = true;
            return node;
        }

        int p = X[0].length;
        int numFeaturesToTry = (maxFeatures != null && maxFeatures > 0 && maxFeatures <= p) ? maxFeatures : p;

        List<Integer> featureList = new ArrayList<>(p);
        for (int j = 0; j < p; j++) featureList.add(j);
        Collections.shuffle(featureList, rng);

        double bestGain = 0.0;
        int bestFeature = -1;
        double bestThreshold = 0.0;
        int[] bestLeftIndices = null;
        int[] bestRightIndices = null;

        double currentVariance = computeVariance(y, indices, nodeMean);

        for (int f = 0; f < numFeaturesToTry; f++) {
            int feat = featureList.get(f);

            // Subsample values or evaluate candidate thresholds
            double[] values = new double[indices.length];
            for (int k = 0; k < indices.length; k++) {
                values[k] = X[indices[k]][feat];
            }
            Arrays.sort(values);

            // Pick quantiles as split candidates to keep tree training fast and balanced
            int step = Math.max(1, values.length / 20);
            for (int s = step; s < values.length - step; s += step) {
                double threshold = (values[s - 1] + values[s]) / 2.0;

                int leftCount = 0;
                for (int idx : indices) {
                    if (X[idx][feat] <= threshold) leftCount++;
                }
                int rightCount = indices.length - leftCount;

                if (leftCount < minSamplesLeaf || rightCount < minSamplesLeaf) {
                    continue;
                }

                int[] leftIdx = new int[leftCount];
                int[] rightIdx = new int[rightCount];
                int l = 0, r = 0;
                for (int idx : indices) {
                    if (X[idx][feat] <= threshold) {
                        leftIdx[l++] = idx;
                    } else {
                        rightIdx[r++] = idx;
                    }
                }

                double leftMean = computeMean(y, leftIdx);
                double rightMean = computeMean(y, rightIdx);
                double leftVar = computeVariance(y, leftIdx, leftMean);
                double rightVar = computeVariance(y, rightIdx, rightMean);

                double weightedVariance = ((double) leftCount / indices.length) * leftVar
                        + ((double) rightCount / indices.length) * rightVar;
                double gain = currentVariance - weightedVariance;

                if (gain > bestGain) {
                    bestGain = gain;
                    bestFeature = feat;
                    bestThreshold = threshold;
                    bestLeftIndices = leftIdx;
                    bestRightIndices = rightIdx;
                }
            }
        }

        if (bestGain <= 1e-7 || bestLeftIndices == null || bestRightIndices == null) {
            node.isLeaf = true;
            return node;
        }

        node.isLeaf = false;
        node.featureIndex = bestFeature;
        node.threshold = bestThreshold;
        node.impurityReduction = bestGain * indices.length;

        if (featureImportances != null && bestFeature >= 0 && bestFeature < featureImportances.length) {
            featureImportances[bestFeature] += node.impurityReduction;
        }

        node.left = buildTree(X, y, bestLeftIndices, currentDepth + 1, rng);
        node.right = buildTree(X, y, bestRightIndices, currentDepth + 1, rng);
        return node;
    }

    private double computeMean(double[] y, int[] indices) {
        if (indices.length == 0) return 0.0;
        double sum = 0.0;
        for (int idx : indices) sum += y[idx];
        return sum / indices.length;
    }

    private double computeVariance(double[] y, int[] indices, double mean) {
        if (indices.length <= 1) return 0.0;
        double sumSq = 0.0;
        for (int idx : indices) {
            double diff = y[idx] - mean;
            sumSq += diff * diff;
        }
        return sumSq / indices.length;
    }

    private void normalizeImportances() {
        if (featureImportances == null) return;
        double sum = 0.0;
        for (double imp : featureImportances) sum += imp;
        if (sum > 1e-9) {
            for (int i = 0; i < featureImportances.length; i++) {
                featureImportances[i] /= sum;
            }
        }
    }

    @Override
    public double predict(double[] x) {
        if (!trained || root == null) {
            throw new IllegalStateException("DecisionTreeRegressor is not trained.");
        }
        Node cur = root;
        while (!cur.isLeaf) {
            if (x[cur.featureIndex] <= cur.threshold) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return cur.prediction;
    }

    @Override
    public String getName() {
        return "Decision Tree Regressor";
    }

    @Override
    public String getAlgorithmType() {
        return "Tree-Based (Variance Reduction)";
    }

    @Override
    public Map<String, Double> getFeatureImportances(String[] featureNames) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (!trained || featureImportances == null || featureNames == null) {
            return Collections.emptyMap();
        }
        for (int i = 0; i < Math.min(featureImportances.length, featureNames.length); i++) {
            map.put(featureNames[i], Math.round(featureImportances[i] * 10000.0) / 10000.0);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public boolean isTrained() {
        return trained;
    }
}
