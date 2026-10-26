package com.studentproductivity.database;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Data Transfer Object for saved prediction history records.
 */
public class HistoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private Timestamp createdAt;
    private int age;
    private String gender;
    private double studyHours;
    private double sleepHours;
    private double phoneUsageHours;
    private double attendancePercentage;
    private int stressLevel;
    private double focusScore;
    private double predictedScore;
    private String modelVersion;
    private String inputJson;

    public HistoryRecord() {}

    public HistoryRecord(long id, Timestamp createdAt, int age, String gender,
                         double studyHours, double sleepHours, double phoneUsageHours,
                         double attendancePercentage, int stressLevel, double focusScore,
                         double predictedScore, String modelVersion, String inputJson) {
        this.id = id;
        this.createdAt = createdAt;
        this.age = age;
        this.gender = gender;
        this.studyHours = studyHours;
        this.sleepHours = sleepHours;
        this.phoneUsageHours = phoneUsageHours;
        this.attendancePercentage = attendancePercentage;
        this.stressLevel = stressLevel;
        this.focusScore = focusScore;
        this.predictedScore = predictedScore;
        this.modelVersion = modelVersion;
        this.inputJson = inputJson;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getStudyHours() { return studyHours; }
    public void setStudyHours(double studyHours) { this.studyHours = studyHours; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public double getPhoneUsageHours() { return phoneUsageHours; }
    public void setPhoneUsageHours(double phoneUsageHours) { this.phoneUsageHours = phoneUsageHours; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public int getStressLevel() { return stressLevel; }
    public void setStressLevel(int stressLevel) { this.stressLevel = stressLevel; }

    public double getFocusScore() { return focusScore; }
    public void setFocusScore(double focusScore) { this.focusScore = focusScore; }

    public double getPredictedScore() { return predictedScore; }
    public void setPredictedScore(double predictedScore) { this.predictedScore = predictedScore; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public String getInputJson() { return inputJson; }
    public void setInputJson(String inputJson) { this.inputJson = inputJson; }
}
