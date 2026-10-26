package com.studentproductivity.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing a student record in the lifestyle and productivity dataset.
 */
public class StudentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final long studentId;
    private final int age;
    private final String gender; // Male, Female, Other
    private final double studyHoursPerDay;
    private final double sleepHours;
    private final double phoneUsageHours;
    private final double socialMediaHours;
    private final double youtubeHours;
    private final double gamingHours;
    private final int breaksPerDay;
    private final double coffeeIntakeMg;
    private final double exerciseMinutes;
    private final int assignmentsCompleted;
    private final double attendancePercentage;
    private final int stressLevel; // 1 to 10
    private final double focusScore; // 0 to 100
    private final double finalGrade; // 0 to 100
    private final Double productivityScore; // Target variable (may be null in test data)

    public StudentRecord(long id, long studentId, int age, String gender,
                         double studyHoursPerDay, double sleepHours, double phoneUsageHours,
                         double socialMediaHours, double youtubeHours, double gamingHours,
                         int breaksPerDay, double coffeeIntakeMg, double exerciseMinutes,
                         int assignmentsCompleted, double attendancePercentage, int stressLevel,
                         double focusScore, double finalGrade, Double productivityScore) {
        this.id = id;
        this.studentId = studentId;
        this.age = age;
        this.gender = (gender == null || gender.trim().isEmpty()) ? "Other" : gender.trim();
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
        this.productivityScore = productivityScore;
    }

    // Getters
    public long getId() { return id; }
    public long getStudentId() { return studentId; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public double getStudyHoursPerDay() { return studyHoursPerDay; }
    public double getSleepHours() { return sleepHours; }
    public double getPhoneUsageHours() { return phoneUsageHours; }
    public double getSocialMediaHours() { return socialMediaHours; }
    public double getYoutubeHours() { return youtubeHours; }
    public double getGamingHours() { return gamingHours; }
    public int getBreaksPerDay() { return breaksPerDay; }
    public double getCoffeeIntakeMg() { return coffeeIntakeMg; }
    public double getExerciseMinutes() { return exerciseMinutes; }
    public int getAssignmentsCompleted() { return assignmentsCompleted; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public int getStressLevel() { return stressLevel; }
    public double getFocusScore() { return focusScore; }
    public double getFinalGrade() { return finalGrade; }
    public Double getProductivityScore() { return productivityScore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentRecord that = (StudentRecord) o;
        return id == that.id && studentId == that.studentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentId);
    }

    @Override
    public String toString() {
        return "StudentRecord{" +
                "id=" + id +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", studyHours=" + studyHoursPerDay +
                ", sleepHours=" + sleepHours +
                ", productivityScore=" + productivityScore +
                '}';
    }
}
