package com.studentproductivity.prediction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Validated user input request for predicting student productivity.
 */
public class PredictionRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int age;
    private String gender; // Male, Female, Other
    private double studyHoursPerDay;
    private double sleepHours;
    private double phoneUsageHours;
    private double socialMediaHours;
    private double youtubeHours;
    private double gamingHours;
    private int breaksPerDay;
    private double coffeeIntakeMg;
    private double exerciseMinutes;
    private int assignmentsCompleted;
    private double attendancePercentage;
    private int stressLevel; // 1 to 10
    private double focusScore; // 0 to 100
    private double finalGrade; // 0 to 100

    public PredictionRequest() {}

    public PredictionRequest(int age, String gender, double studyHoursPerDay, double sleepHours,
                             double phoneUsageHours, double socialMediaHours, double youtubeHours,
                             double gamingHours, int breaksPerDay, double coffeeIntakeMg,
                             double exerciseMinutes, int assignmentsCompleted,
                             double attendancePercentage, int stressLevel,
                             double focusScore, double finalGrade) {
        this.age = age;
        this.gender = gender;
        this.studyHoursPerDay = studyHoursPerDay;
        this.sleepHours = sleepHours;
        this.phoneUsageHours = phoneUsageHours;
        this.socialMediaHours = socialMediaHours;
        this.youtubeHours = youtubeHours;
        this.gamingHours = gamingHours;
        this.breaksPerDay = breaksPerDay;
        this.coffeeIntakeMg = coffeeIntakeMg;
        this.exerciseMinutes = exerciseMinutes;
        this.assignmentsCompleted = assignmentsCompleted;
        this.attendancePercentage = attendancePercentage;
        this.stressLevel = stressLevel;
        this.focusScore = focusScore;
        this.finalGrade = finalGrade;
    }

    /**
     * Validates input values against sensible realistic educational bounds.
     * Returns a list of error messages (empty if valid).
     */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (age < 15 || age > 99) {
            errors.add("Age must be between 15 and 99 years.");
        }
        if (gender == null || gender.trim().isEmpty()) {
            errors.add("Gender selection is required.");
        }
        if (studyHoursPerDay < 0.0 || studyHoursPerDay > 24.0) {
            errors.add("Study hours must be between 0.0 and 24.0 hours/day.");
        }
        if (sleepHours < 0.0 || sleepHours > 24.0) {
            errors.add("Sleep hours must be between 0.0 and 24.0 hours/day.");
        }
        if (studyHoursPerDay + sleepHours > 24.0) {
            errors.add("Combined study hours (" + studyHoursPerDay + "h) and sleep hours (" + sleepHours + "h) cannot exceed 24 hours in a single day.");
        }
        if (phoneUsageHours < 0.0 || phoneUsageHours > 24.0) {
            errors.add("Phone usage must be between 0.0 and 24.0 hours/day.");
        }
        if (socialMediaHours < 0.0 || socialMediaHours > 24.0) {
            errors.add("Social media hours must be between 0.0 and 24.0 hours/day.");
        }
        if (youtubeHours < 0.0 || youtubeHours > 24.0) {
            errors.add("YouTube hours must be between 0.0 and 24.0 hours/day.");
        }
        if (gamingHours < 0.0 || gamingHours > 24.0) {
            errors.add("Gaming hours must be between 0.0 and 24.0 hours/day.");
        }
        if (breaksPerDay < 0 || breaksPerDay > 50) {
            errors.add("Breaks per day must be between 0 and 50.");
        }
        if (coffeeIntakeMg < 0.0 || coffeeIntakeMg > 2000.0) {
            errors.add("Coffee intake must be between 0 and 2000 mg.");
        }
        if (exerciseMinutes < 0.0 || exerciseMinutes > 600.0) {
            errors.add("Exercise minutes must be between 0 and 600 minutes/day.");
        }
        if (assignmentsCompleted < 0 || assignmentsCompleted > 100) {
            errors.add("Assignments completed must be between 0 and 100.");
        }
        if (attendancePercentage < 0.0 || attendancePercentage > 100.0) {
            errors.add("Attendance percentage must be between 0.0% and 100.0%.");
        }
        if (stressLevel < 1 || stressLevel > 10) {
            errors.add("Stress level rating must be on a scale from 1 to 10.");
        }
        if (focusScore < 0.0 || focusScore > 100.0) {
            errors.add("Focus score must be between 0 and 100.");
        }
        if (finalGrade < 0.0 || finalGrade > 100.0) {
            errors.add("Academic grade must be between 0.0 and 100.0.");
        }
        return errors;
    }

    // Getters and Setters
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getStudyHoursPerDay() { return studyHoursPerDay; }
    public void setStudyHoursPerDay(double studyHoursPerDay) { this.studyHoursPerDay = studyHoursPerDay; }

    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }

    public double getPhoneUsageHours() { return phoneUsageHours; }
    public void setPhoneUsageHours(double phoneUsageHours) { this.phoneUsageHours = phoneUsageHours; }

    public double getSocialMediaHours() { return socialMediaHours; }
    public void setSocialMediaHours(double socialMediaHours) { this.socialMediaHours = socialMediaHours; }

    public double getYoutubeHours() { return youtubeHours; }
    public void setYoutubeHours(double youtubeHours) { this.youtubeHours = youtubeHours; }

    public double getGamingHours() { return gamingHours; }
    public void setGamingHours(double gamingHours) { this.gamingHours = gamingHours; }

    public int getBreaksPerDay() { return breaksPerDay; }
    public void setBreaksPerDay(int breaksPerDay) { this.breaksPerDay = breaksPerDay; }

    public double getCoffeeIntakeMg() { return coffeeIntakeMg; }
    public void setCoffeeIntakeMg(double coffeeIntakeMg) { this.coffeeIntakeMg = coffeeIntakeMg; }

    public double getExerciseMinutes() { return exerciseMinutes; }
    public void setExerciseMinutes(double exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }

    public int getAssignmentsCompleted() { return assignmentsCompleted; }
    public void setAssignmentsCompleted(int assignmentsCompleted) { this.assignmentsCompleted = assignmentsCompleted; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public int getStressLevel() { return stressLevel; }
    public void setStressLevel(int stressLevel) { this.stressLevel = stressLevel; }

    public double getFocusScore() { return focusScore; }
    public void setFocusScore(double focusScore) { this.focusScore = focusScore; }

    public double getFinalGrade() { return finalGrade; }
    public void setFinalGrade(double finalGrade) { this.finalGrade = finalGrade; }
}
