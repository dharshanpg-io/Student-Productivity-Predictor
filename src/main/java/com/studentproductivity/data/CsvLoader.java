package com.studentproductivity.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Robust CSV Loader for student productivity and lifestyle datasets.
 * Performs schema validation, range checks, missing value detection, and audit logging.
 */
public class CsvLoader {

    public static class LoadingReport {
        private int totalLinesRead = 0;
        private int validRecordsLoaded = 0;
        private int skippedRowsCount = 0;
        private int duplicateRowsCount = 0;
        private final List<String> warningMessages = new ArrayList<>();
        private List<String> headerColumns = new ArrayList<>();
        private long executionTimeMs = 0;

        public int getTotalLinesRead() { return totalLinesRead; }
        public int getValidRecordsLoaded() { return validRecordsLoaded; }
        public int getSkippedRowsCount() { return skippedRowsCount; }
        public int getDuplicateRowsCount() { return duplicateRowsCount; }
        public List<String> getWarningMessages() { return Collections.unmodifiableList(warningMessages); }
        public List<String> getHeaderColumns() { return Collections.unmodifiableList(headerColumns); }
        public long getExecutionTimeMs() { return executionTimeMs; }
    }

    public static class LoadResult {
        private final Dataset dataset;
        private final LoadingReport report;

        public LoadResult(Dataset dataset, LoadingReport report) {
            this.dataset = dataset;
            this.report = report;
        }

        public Dataset getDataset() { return dataset; }
        public LoadingReport getReport() { return report; }
    }

    public LoadResult loadCsv(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("Dataset file not found: " + (file != null ? file.getAbsolutePath() : "null"));
        }

        long startTime = System.currentTimeMillis();
        LoadingReport report = new LoadingReport();
        List<StudentRecord> records = new ArrayList<>();
        Set<Long> seenIds = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is completely empty: " + file.getAbsolutePath());
            }

            // Parse header columns
            String[] rawHeaders = parseCsvLine(headerLine);
            Map<String, Integer> colIndexMap = new HashMap<>();
            List<String> cleanHeaders = new ArrayList<>();

            for (int i = 0; i < rawHeaders.length; i++) {
                String h = rawHeaders[i].trim().toLowerCase();
                colIndexMap.put(h, i);
                cleanHeaders.add(rawHeaders[i].trim());
            }
            report.headerColumns = cleanHeaders;

            // Validate mandatory column presence
            validateExpectedHeaders(colIndexMap);

            boolean hasTarget = colIndexMap.containsKey("productivity_score");
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                report.totalLinesRead++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] tokens = parseCsvLine(line);
                if (tokens.length < colIndexMap.size()) {
                    report.skippedRowsCount++;
                    if (report.warningMessages.size() < 20) {
                        report.warningMessages.add("Line " + lineNumber + ": Insufficient columns (" + tokens.length + " vs expected " + colIndexMap.size() + ")");
                    }
                    continue;
                }

                try {
                    long id = parseLong(tokens, colIndexMap.get("id"), lineNumber, "id");
                    long studentId = colIndexMap.containsKey("student_id") ? parseLong(tokens, colIndexMap.get("student_id"), lineNumber, "student_id") : id;

                    // Duplicate check based on ID
                    if (seenIds.contains(id)) {
                        report.duplicateRowsCount++;
                        report.skippedRowsCount++;
                        continue;
                    }

                    int age = parseInt(tokens, colIndexMap.get("age"), lineNumber, "age", 15, 100);
                    String gender = parseString(tokens, colIndexMap.get("gender"));

                    double studyHours = parseDouble(tokens, colIndexMap.get("study_hours_per_day"), lineNumber, "study_hours_per_day", 0.0, 24.0);
                    double sleepHours = parseDouble(tokens, colIndexMap.get("sleep_hours"), lineNumber, "sleep_hours", 0.0, 24.0);
                    double phoneUsage = parseDouble(tokens, colIndexMap.get("phone_usage_hours"), lineNumber, "phone_usage_hours", 0.0, 24.0);
                    double socialMedia = parseDouble(tokens, colIndexMap.get("social_media_hours"), lineNumber, "social_media_hours", 0.0, 24.0);
                    double youtube = parseDouble(tokens, colIndexMap.get("youtube_hours"), lineNumber, "youtube_hours", 0.0, 24.0);
                    double gaming = parseDouble(tokens, colIndexMap.get("gaming_hours"), lineNumber, "gaming_hours", 0.0, 24.0);
                    int breaks = parseInt(tokens, colIndexMap.get("breaks_per_day"), lineNumber, "breaks_per_day", 0, 50);
                    double coffee = parseDouble(tokens, colIndexMap.get("coffee_intake_mg"), lineNumber, "coffee_intake_mg", 0.0, 2000.0);
                    double exercise = parseDouble(tokens, colIndexMap.get("exercise_minutes"), lineNumber, "exercise_minutes", 0.0, 600.0);
                    int assignments = parseInt(tokens, colIndexMap.get("assignments_completed"), lineNumber, "assignments_completed", 0, 100);
                    double attendance = parseDouble(tokens, colIndexMap.get("attendance_percentage"), lineNumber, "attendance_percentage", 0.0, 100.0);
                    int stress = parseInt(tokens, colIndexMap.get("stress_level"), lineNumber, "stress_level", 1, 10);
                    double focus = parseDouble(tokens, colIndexMap.get("focus_score"), lineNumber, "focus_score", 0.0, 100.0);
                    double finalGrade = parseDouble(tokens, colIndexMap.get("final_grade"), lineNumber, "final_grade", 0.0, 100.0);

                    Double target = null;
                    if (hasTarget) {
                        target = parseDouble(tokens, colIndexMap.get("productivity_score"), lineNumber, "productivity_score", 0.0, 100.0);
                    }

                    StudentRecord record = new StudentRecord(id, studentId, age, gender, studyHours, sleepHours,
                            phoneUsage, socialMedia, youtube, gaming, breaks, coffee, exercise,
                            assignments, attendance, stress, focus, finalGrade, target);

                    records.add(record);
                    seenIds.add(id);
                    report.validRecordsLoaded++;

                } catch (IllegalArgumentException ex) {
                    report.skippedRowsCount++;
                    if (report.warningMessages.size() < 25) {
                        report.warningMessages.add("Line " + lineNumber + " skipped: " + ex.getMessage());
                    }
                }
            }
        }

        report.executionTimeMs = System.currentTimeMillis() - startTime;
        Dataset dataset = new Dataset(records, report.headerColumns);
        return new LoadResult(dataset, report);
    }

    private void validateExpectedHeaders(Map<String, Integer> colMap) throws IOException {
        String[] essentialFeatures = {
                "age", "study_hours_per_day", "sleep_hours", "phone_usage_hours",
                "attendance_percentage", "stress_level", "focus_score"
        };
        for (String feat : essentialFeatures) {
            if (!colMap.containsKey(feat)) {
                throw new IOException("Missing essential column in CSV schema: '" + feat + "'. Please check the dataset format.");
            }
        }
    }

    private long parseLong(String[] tokens, Integer idx, int line, String field) {
        if (idx == null || idx >= tokens.length) {
            throw new IllegalArgumentException("Missing field '" + field + "'");
        }
        try {
            return (long) Double.parseDouble(tokens[idx].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for '" + field + "': " + tokens[idx]);
        }
    }

    private int parseInt(String[] tokens, Integer idx, int line, String field, int min, int max) {
        if (idx == null || idx >= tokens.length) {
            throw new IllegalArgumentException("Missing field '" + field + "'");
        }
        try {
            int val = (int) Math.round(Double.parseDouble(tokens[idx].trim()));
            if (val < min || val > max) {
                // Clamp or throw
                val = Math.max(min, Math.min(max, val));
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer for '" + field + "': " + tokens[idx]);
        }
    }

    private double parseDouble(String[] tokens, Integer idx, int line, String field, double min, double max) {
        if (idx == null || idx >= tokens.length) {
            throw new IllegalArgumentException("Missing field '" + field + "'");
        }
        try {
            double val = Double.parseDouble(tokens[idx].trim());
            if (Double.isNaN(val) || Double.isInfinite(val)) {
                throw new IllegalArgumentException("Non-finite number for '" + field + "'");
            }
            if (val < min || val > max) {
                val = Math.max(min, Math.min(max, val));
            }
            return val;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numerical value for '" + field + "': " + tokens[idx]);
        }
    }

    private String parseString(String[] tokens, Integer idx) {
        if (idx == null || idx >= tokens.length) {
            return "Other";
        }
        String s = tokens[idx].trim();
        return s.isEmpty() ? "Other" : s;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                result.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        result.add(cur.toString());
        return result.toArray(new String[0]);
    }
}
