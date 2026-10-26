package com.studentproductivity.data;

import java.io.Serializable;
import java.util.*;

/**
 * Encapsulates the loaded dataset, column schema, and descriptive statistical summaries.
 */
public class Dataset implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<StudentRecord> records;
    private final List<String> columnNames;
    private final Map<String, FeatureStats> statisticsMap = new LinkedHashMap<>();
    private final Map<String, Double> correlationsWithTarget = new LinkedHashMap<>();

    public Dataset(List<StudentRecord> records, List<String> columnNames) {
        this.records = (records != null) ? new ArrayList<>(records) : new ArrayList<>();
        this.columnNames = (columnNames != null) ? new ArrayList<>(columnNames) : new ArrayList<>();
        calculateStatistics();
        calculateCorrelations();
    }

    public List<StudentRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public int size() {
        return records.size();
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    public FeatureStats getStats(String featureName) {
        return statisticsMap.get(featureName);
    }

    public Map<String, FeatureStats> getAllStatistics() {
        return Collections.unmodifiableMap(statisticsMap);
    }

    public Map<String, Double> getCorrelationsWithTarget() {
        return Collections.unmodifiableMap(correlationsWithTarget);
    }

    public static class FeatureStats implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String featureName;
        private final int count;
        private final double min;
        private final double max;
        private final double mean;
        private final double standardDeviation;
        private final double median;
        private final int missingCount;

        public FeatureStats(String featureName, int count, double min, double max,
                            double mean, double standardDeviation, double median, int missingCount) {
            this.featureName = featureName;
            this.count = count;
            this.min = min;
            this.max = max;
            this.mean = mean;
            this.standardDeviation = standardDeviation;
            this.median = median;
            this.missingCount = missingCount;
        }

        public String getFeatureName() { return featureName; }
        public int getCount() { return count; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public double getMean() { return mean; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getMedian() { return median; }
        public int getMissingCount() { return missingCount; }
    }

    private void calculateStatistics() {
        if (records.isEmpty()) return;

        computeColumnStats("age", r -> (double) r.getAge());
        computeColumnStats("study_hours_per_day", StudentRecord::getStudyHoursPerDay);
        computeColumnStats("sleep_hours", StudentRecord::getSleepHours);
        computeColumnStats("phone_usage_hours", StudentRecord::getPhoneUsageHours);
        computeColumnStats("social_media_hours", StudentRecord::getSocialMediaHours);
        computeColumnStats("youtube_hours", StudentRecord::getYoutubeHours);
        computeColumnStats("gaming_hours", StudentRecord::getGamingHours);
        computeColumnStats("breaks_per_day", r -> (double) r.getBreaksPerDay());
        computeColumnStats("coffee_intake_mg", StudentRecord::getCoffeeIntakeMg);
        computeColumnStats("exercise_minutes", StudentRecord::getExerciseMinutes);
        computeColumnStats("assignments_completed", r -> (double) r.getAssignmentsCompleted());
        computeColumnStats("attendance_percentage", StudentRecord::getAttendancePercentage);
        computeColumnStats("stress_level", r -> (double) r.getStressLevel());
        computeColumnStats("focus_score", StudentRecord::getFocusScore);
        computeColumnStats("final_grade", StudentRecord::getFinalGrade);

        if (hasTarget()) {
            computeColumnStats("productivity_score", StudentRecord::getProductivityScore);
        }
    }

    public boolean hasTarget() {
        return !records.isEmpty() && records.get(0).getProductivityScore() != null;
    }

    private interface Extractor {
        Double extract(StudentRecord r);
    }

    private void computeColumnStats(String name, Extractor extractor) {
        List<Double> values = new ArrayList<>(records.size());
        int missing = 0;
        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;

        for (StudentRecord r : records) {
            Double v = extractor.extract(r);
            if (v == null || Double.isNaN(v)) {
                missing++;
            } else {
                values.add(v);
                sum += v;
                if (v < min) min = v;
                if (v > max) max = v;
            }
        }

        if (values.isEmpty()) {
            statisticsMap.put(name, new FeatureStats(name, 0, 0, 0, 0, 0, 0, missing));
            return;
        }

        double mean = sum / values.size();
        double varianceSum = 0.0;
        for (double v : values) {
            double diff = v - mean;
            varianceSum += diff * diff;
        }
        double std = Math.sqrt(varianceSum / values.size());

        Collections.sort(values);
        double median;
        int mid = values.size() / 2;
        if (values.size() % 2 == 0) {
            median = (values.get(mid - 1) + values.get(mid)) / 2.0;
        } else {
            median = values.get(mid);
        }

        statisticsMap.put(name, new FeatureStats(name, values.size(), min, max, mean, std, median, missing));
    }

    private void calculateCorrelations() {
        if (!hasTarget()) return;

        double[] target = new double[records.size()];
        for (int i = 0; i < records.size(); i++) {
            target[i] = records.get(i).getProductivityScore();
        }

        calculateSingleCorrelation("study_hours_per_day", StudentRecord::getStudyHoursPerDay, target);
        calculateSingleCorrelation("sleep_hours", StudentRecord::getSleepHours, target);
        calculateSingleCorrelation("phone_usage_hours", StudentRecord::getPhoneUsageHours, target);
        calculateSingleCorrelation("social_media_hours", StudentRecord::getSocialMediaHours, target);
        calculateSingleCorrelation("youtube_hours", StudentRecord::getYoutubeHours, target);
        calculateSingleCorrelation("gaming_hours", StudentRecord::getGamingHours, target);
        calculateSingleCorrelation("breaks_per_day", r -> (double) r.getBreaksPerDay(), target);
        calculateSingleCorrelation("coffee_intake_mg", StudentRecord::getCoffeeIntakeMg, target);
        calculateSingleCorrelation("exercise_minutes", StudentRecord::getExerciseMinutes, target);
        calculateSingleCorrelation("assignments_completed", r -> (double) r.getAssignmentsCompleted(), target);
        calculateSingleCorrelation("attendance_percentage", StudentRecord::getAttendancePercentage, target);
        calculateSingleCorrelation("stress_level", r -> (double) r.getStressLevel(), target);
        calculateSingleCorrelation("focus_score", StudentRecord::getFocusScore, target);
        calculateSingleCorrelation("final_grade", StudentRecord::getFinalGrade, target);
        calculateSingleCorrelation("age", r -> (double) r.getAge(), target);
    }

    private void calculateSingleCorrelation(String name, Extractor extractor, double[] target) {
        int n = records.size();
        double sumX = 0, sumY = 0;
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            Double val = extractor.extract(records.get(i));
            x[i] = (val != null) ? val : 0.0;
            sumX += x[i];
            sumY += target[i];
        }

        double meanX = sumX / n;
        double meanY = sumY / n;

        double num = 0, denX = 0, denY = 0;
        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = target[i] - meanY;
            num += dx * dy;
            denX += dx * dx;
            denY += dy * dy;
        }

        double denom = Math.sqrt(denX * denY);
        double corr = (denom > 1e-9) ? (num / denom) : 0.0;
        correlationsWithTarget.put(name, Math.round(corr * 10000.0) / 10000.0);
    }
}
