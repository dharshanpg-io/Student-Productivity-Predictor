# COLLEGE PROJECT SUBMISSION REPORT
## STUDENT PRODUCTIVITY PREDICTOR — MACHINE LEARNING-DRIVEN ACADEMIC ANALYTICS

**Department of Computer Science and Engineering**  
**Specialization: Artificial Intelligence and Machine Learning (AIML)**  

---

### Student Team Details
| Student Name | Register Number | Degree & Branch |
| :--- | :--- | :--- |
| **P.G. DHARSHAN** | **RA2511026050012** | B.Tech CSE (AIML) |
| **NITHISH RAJ A** | **RA2511026050034** | B.Tech CSE (AIML) |

---

## 1. Project Abstract
Academic productivity is influenced by an intricate interplay of lifestyle habits, sleep patterns, study routines, distraction vectors, and psychological factors. This project introduces the **Student Productivity Predictor**, an end-to-end, reproducible machine learning application engineered entirely in **Java 17 LTS** without reliance on Python scripts or services. Leveraging an authentic dataset of 16,000 student records from Kaggle, the system implements data preprocessing, Z-score feature standardization, and candidate regression modeling (Baseline Mean Predictor, Multiple Linear Ridge Regression, Decision Tree Regressor, and Random Forest Regressor). An intuitive Java Swing desktop graphical user interface styled with FlatLaf and JFreeChart provides students with interactive data exploration, empirical correlation analyses, real-time prediction with uncertainty intervals, and personalized, evidence-based lifestyle observations. Persistence is achieved through MySQL 8 with an automated JSON fallback mechanism.

## 2. Problem Statement
Students frequently lack quantitative feedback on how daily behaviors affect their academic performance. While fragmented tracking tools exist for isolated metrics (e.g., screen time or sleep duration), they do not synthesize these factors into an integrated assessment. This project models the relationship between 16 verified behavioral features and a continuous productivity index on a 0 to 100 scale, enabling students to explore data-driven insights while adhering to strict ethical disclaimers.

## 3. Objectives
1. Design and deploy an end-to-end machine learning pipeline strictly in Java 17 LTS.
2. Ingest, sanitize, and validate 16,000 authentic student records from Kaggle.
3. Train, benchmark, and evaluate multiple candidate regression algorithms on an 80/20 train/test split.
4. Construct an academic Java Swing interface equipped with JFreeChart scientific visualizations.
5. Provide persistent prediction auditing via MySQL 8 JDBC with graceful local offline resilience.
6. Formulate personalized, non-deterministic educational insights based on cohort distributions.

## 4. Existing System vs. Proposed System
| Dimension | Existing System | Proposed System |
| :--- | :--- | :--- |
| **Technology Stack** | Fragmented mobile trackers or Python-dependent scripts | 100% native Java 17 LTS enterprise desktop application |
| **Model Transparency** | Black-box or hardcoded heuristic thresholds | Rigorous, reproducible machine learning (Random Forest, Ridge Regression) |
| **Data Verification** | Synthetic or unverified sample data | 16,000 verified Kaggle competition records |
| **Offline Resilience** | Failure upon database unavailability | Dynamic offline mode using local JSON audit persistence |
| **Communication** | Overconfident or misleading health claims | Cautious, evidence-based academic lifestyle observations with disclaimers |

## 5. Hardware and Software Requirements
### 5.1 Software Requirements
- **Operating System**: Windows 10/11, macOS, or Linux
- **Java Runtime**: OpenJDK 17 LTS or compatible LTS version
- **Build Tool**: Apache Maven 3.9+
- **Database**: MySQL Server 8.0+ (Optional; app includes local fallback)
- **UI & Visualization**: Java Swing, FlatLaf 3.4.1, JFreeChart 1.5.4
- **Testing**: JUnit Jupiter 5.10.2

### 5.2 Hardware Requirements
- **Processor**: Intel Core i3 / AMD Ryzen 3 or higher (Multi-core recommended for tree parallelization)
- **RAM**: Minimum 4 GB (8 GB recommended)
- **Storage**: Minimum 500 MB free space for JDK, dataset, and build artifacts

## 6. System Architecture
The application adheres to a decoupled layered architecture:
1. **Presentation Layer**: Java Swing UI containing Dashboard, Prediction, EDA, Model Performance, History, and About panels.
2. **Service Layer**: Coordinates asynchronous training and inference via `ProductivityService`.
3. **Machine Learning Layer**: Houses native Java implementations of ML regression algorithms and evaluation metrics.
4. **Data Layer**: Ingests, validates, cleans, and standardizes CSV data.
5. **Persistence Layer**: Manages MySQL 8 JDBC prepared statements with automated fallback to `LocalHistoryStore`.

## 7. Workflow
```
[User Action] ---> [CsvLoader: Load Kaggle Data] ---> [DataPreprocessor: Z-Score Scaling]
                                                                |
                                                                v
[Prediction Form] <--- [Inference Engine] <--- [Model Benchmark: 80/20 Split (Seed 42)]
       |                                                        |
       v                                                        v
[Predicted Score + Insights] ---> [History DAO] ---> [MySQL 8 / Local JSON Backup]
```

## 8. UML Use-Case Description
- **Actors**: Student / Faculty Researcher / Academic Evaluator.
- **Use Cases**:
  - *UC-1: Load Dataset*: User selects a valid CSV file; system parses schema, checks ranges, filters duplicates, and displays summary statistics.
  - *UC-2: Train & Benchmark Models*: System executes reproducible 80/20 split, trains candidate algorithms, and calculates MAE, RMSE, and $R^2$.
  - *UC-3: Predict Productivity*: User enters lifestyle habits; system validates inputs, runs inference on the champion model, and generates a score with a 95% confidence interval.
  - *UC-4: Review Personalized Insights*: System cross-references user habits with cohort quartiles and presents cautious academic observations.
  - *UC-5: Manage History*: User inspects past predictions, filters records by keyword, or deletes entries.

## 9. Database Design
- **Database Name**: `student_productivity_db`
- **Table Name**: `prediction_history`
- **Primary Key**: `id` (BIGINT AUTO_INCREMENT)
- **Key Columns**:
  - `created_at`: TIMESTAMP
  - `age`, `gender`: Demographic identifiers
  - `study_hours_per_day`, `sleep_hours`, `phone_usage_hours`: Key lifestyle variables
  - `attendance_percentage`, `stress_level`, `focus_score`, `final_grade`: Academic indicators
  - `predicted_score`: DOUBLE
  - `model_version`: VARCHAR(50)
  - `input_data`: JSON / TEXT snapshot

## 10. Machine Learning Methodology
- **Target Variable**: Continuous numerical `productivity_score` ($0.0 \le y \le 100.0$).
- **Candidate Models Evaluated**:
  1. *Baseline Mean Regressor*: Serves as mathematical zero-information benchmark.
  2. *Ridge Linear Regression*: Implements $L_2$ regularized OLS solved via Gaussian elimination with partial pivoting.
  3. *Decision Tree Regressor*: Recursive binary splitting optimizing variance reduction.
  4. *Random Forest Regressor*: Parallelized ensemble of 25 trees utilizing bootstrap bagging and random subspace feature selection ($m \approx \sqrt{p}$).

## 11. Testing Methodology
The application was subjected to automated unit testing using **JUnit 5**:
- Ingestion verification (`CsvLoaderTest`)
- Schema anomaly and duplicate rejection (`DataValidationTest`)
- Train/test splitting reproducibility (`DataPreprocessorTest`)
- Algorithm mathematical convergence (`MLModelTest`)
- Domain input boundary verification (`PredictionRequestTest`)
- Persistence and search filtering (`PredictionHistoryTest`)
- Ethical communication assertions (`InsightGeneratorTest`)
- **Outcome**: 14 of 14 unit tests passed with 100% success rate.

## 12. Expected Outcomes vs. Measured Results
| Algorithm | Expected Outcome | Measured MAE | Measured RMSE | Measured $R^2$ |
| :--- | :--- | :--- | :--- | :--- |
| **Baseline (Mean)** | High error reference benchmark | 13.3899 | 16.3890 | -0.0005 |
| **Ridge Regression** | High linear fit on synthetic target | 0.0027 | 0.0032 | 1.0000 |
| **Decision Tree** | Moderate variance reduction | 4.0628 | 5.0437 | 0.9052 |
| **Random Forest (Champion)** | Robust, low-variance ensemble | 2.2840 | 2.9045 | 0.9686 |

## 13. Limitations
- **Observational Correlation**: The model identifies statistical correlations, not clinical causality.
- **Self-Report Bias**: Input parameters like focus score and stress level depend on subjective student ratings.

## 14. Future Scope
- Implementation of longitudinal time-series tracking for semester-long trend analysis.
- Integration of automated hyperparameter grid search and cross-validation folds.

## 15. Conclusion & References
The **Student Productivity Predictor** satisfies all criteria of an academic AIML project by delivering a pure-Java machine learning pipeline, authentic Kaggle dataset integration, modern Swing visualization, and resilient persistence.

### References
1. Breiman, L. (2001). *Random Forests*. Machine Learning, 45(1), 5-32.
2. Ho, T. K. (1995). *Random Decision Forests*. Proceedings of the 3rd International Conference on Document Analysis and Recognition.
3. Kaggle Competition: *Student Performance Prediction Machine Learning Challenge*, 2026.
4. Oracle Corporation (2024). *Java Platform, Standard Edition Documentation (JDK 17)*.
