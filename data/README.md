# Dataset Directory

This directory contains the authentic student lifestyle and academic dataset used for training, evaluating, and validating the **Student Productivity Predictor** machine learning models.

## Dataset Origin
- **Source**: Kaggle Competition — *Student Performance Prediction Machine Learning Challenge*
- **URL**: `https://www.kaggle.com/competitions/student-performance-prediction-machine-learning-challenge/data`
- **Total Records**: 16,000 verified rows
- **Target Variable**: `productivity_score` (continuous numerical score from 0.0 to 100.0)

## Verified Column Schema (19 Columns)
| Column Name | Data Type | Permitted Range | Description |
| :--- | :--- | :--- | :--- |
| `id` | Integer (Primary Key) | 1 to 20000 | Unique sequence identifier |
| `student_id` | Integer | 2 to 20000 | Student participant identifier |
| `age` | Integer | 15 to 99 | Student age in years |
| `gender` | Categorical | Male, Female, Other | Student gender |
| `study_hours_per_day` | Double | 0.0 to 24.0 | Dedicated daily study duration |
| `sleep_hours` | Double | 0.0 to 24.0 | Average nightly sleep hours |
| `phone_usage_hours` | Double | 0.0 to 24.0 | Daily smartphone screen time |
| `social_media_hours` | Double | 0.0 to 24.0 | Time spent on social networks |
| `youtube_hours` | Double | 0.0 to 24.0 | Daily YouTube viewing time |
| `gaming_hours` | Double | 0.0 to 24.0 | Daily video gaming duration |
| `breaks_per_day` | Integer | 0 to 50 | Rest intervals taken per study day |
| `coffee_intake_mg` | Double | 0 to 2000 | Daily caffeine consumption |
| `exercise_minutes` | Double | 0 to 600 | Daily physical activity duration |
| `assignments_completed` | Integer | 0 to 100 | Coursework assignments completed |
| `attendance_percentage` | Double | 0.0% to 100.0% | Academic attendance record |
| `stress_level` | Integer | 1 to 10 | Self-reported stress index |
| `focus_score` | Double | 0.0 to 100.0 | Subjective focus rating |
| `final_grade` | Double | 0.0 to 100.0 | Previous term academic grade |
| `productivity_score` | Double | 0.0 to 100.0 | **Target**: Composite productivity index |

## Empirical Dataset Statistics (16,000 Rows)
- **Mean Productivity Score**: 52.41 points
- **Top Positive Correlation**: `study_hours_per_day` ($r = +0.7323$)
- **Second Positive Correlation**: `focus_score` ($r = +0.4151$)
- **Third Positive Correlation**: `sleep_hours` ($r = +0.3386$)
- **Top Inverse Correlation**: `phone_usage_hours` ($r = -0.3269$)
- **Missing Values**: 0 unhandled rows (all numerical ranges verified)
