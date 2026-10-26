# Student Productivity Predictor — Complete Java AIML Project

[![Java 17](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://adoptium.net/)
[![Maven 3.9+](https://img.shields.io/badge/Maven-3.9%2B-blue.svg)](https://maven.apache.org/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10.2-green.svg)](https://junit.org/junit5/)
[![Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

A complete, robust, pure-Java Machine Learning desktop application developed for **Computer Science and Engineering (Artificial Intelligence and Machine Learning)**.

The application analyzes student lifestyle, sleep routines, study habits, digital distractions, and academic factors to predict an estimated **Productivity Score (0 to 100)** using real patterns learned from 16,000 verified records downloaded from Kaggle.

---

## Academic Team Details

| Student Name | Register Number | Role |
| :--- | :--- | :--- |
| **P.G. DHARSHAN** | **RA2511026050012** | AIML Student Developer 1 |
| **NITHISH RAJ A** | **RA2511026050034** | AIML Student Developer 2 |

---

## Key Highlights

- **100% Pure Java**: No Python, Flask, Django, Streamlit, or external Python-based microservices. All data parsing, preprocessing, model training, and inference run directly on the JVM.
- **Genuine Kaggle Dataset**: Pre-loaded with 16,000 verified records from the *Student Performance Prediction Machine Learning Challenge*.
- **Authentic ML Models**: Implements Random Forest Regressor, Multiple Ridge Linear Regression, Decision Tree Regressor, and Baseline Mean Predictor.
- **Modern Desktop UI**: Modern Java Swing interface styled with **FlatLaf Light** and interactive scientific charts via **JFreeChart**.
- **Resilient Persistence**: MySQL 8 JDBC prepared statements with automated fallback to local JSON storage if MySQL is offline.
- **Personalized Insights**: Generates evidence-based observations comparing user habits against cohort quartiles with ethical academic disclaimers.

---

## System Requirements

| Component | Minimum Requirement | Recommended |
| :--- | :--- | :--- |
| **Operating System** | Windows 10/11, macOS, Linux | Windows 11 (64-bit) |
| **Java Development Kit** | OpenJDK 17 LTS | Microsoft OpenJDK 17 or Eclipse Temurin 17 |
| **Build Tool** | Apache Maven 3.8+ | Apache Maven 3.9.6 |
| **Database** | MySQL Server 8.0+ (Optional) | MySQL Server 8.0 on port 3306 |
| **RAM** | 4 GB | 8 GB |

---

## Project Structure

```
APP_PROJECT/
├── pom.xml                               # Maven project object model and dependencies
├── README.md                             # Comprehensive project documentation
├── .gitignore                            # Version control exclusion rules
├── data/
│   ├── README.md                         # Dataset schema and origin details
│   ├── train.csv                         # 16,000 verified Kaggle training records
│   └── test.csv                          # Kaggle test partition
├── models/
│   ├── README.md                         # Model documentation & measured benchmarks
│   └── trained_productivity_model.model  # Serialized champion Random Forest bundle
├── sql/
│   └── schema.sql                        # MySQL 8 database and table creation script
├── docs/
│   ├── problem-statement.md              # Formal academic problem statement
│   ├── system-design.md                  # Architectural diagrams & specifications
│   ├── testing.md                        # Complete test plan and JUnit results
│   └── college-project-report.md         # 15-section report for university submission
└── src/
    ├── main/
    │   ├── java/com/studentproductivity/
    │   │   ├── Main.java                 # Desktop application entry point
    │   │   ├── TrainModelCli.java        # CLI training and evaluation utility
    │   │   ├── config/AppConfig.java     # Configuration manager
    │   │   ├── data/                     # CsvLoader, Dataset, StudentRecord, Preprocessor
    │   │   ├── model/                    # MLModel, LinearRegression, DecisionTree, RandomForest, Evaluator
    │   │   ├── prediction/               # PredictionRequest, PredictionResult, InsightGenerator
    │   │   ├── database/                 # DatabaseManager, PredictionHistoryDao, LocalHistoryStore
    │   │   ├── service/                  # ProductivityService orchestrator
    │   │   ├── ui/                       # Modern Swing panels (Dashboard, Predict, EDA, Model, History, About)
    │   │   └── util/UiUtils.java         # Typography, styling, card helpers
    │   └── resources/
    │       ├── app.properties            # Metadata and team names
    │       ├── database.properties       # MySQL connection credentials
    │       └── database.properties.example
    └── test/java/com/studentproductivity/ # 14 JUnit 5 unit tests
```

---

## Setup & Running Instructions

### 1. Environment Verification
Open your terminal (PowerShell, Command Prompt, or Bash) and verify your Java and Maven installations:
```bash
java -version
mvn -version
```
*Note: Java version must be 17 or higher.*

### 2. Configure Database (Optional)
If you wish to persist history to MySQL 8:
1. Log in to MySQL:
   ```bash
   mysql -u root -p
   ```
2. Execute the schema script:
   ```sql
   SOURCE sql/schema.sql;
   ```
3. Update `src/main/resources/database.properties` with your database credentials.
*Note: If MySQL is not running, the application will automatically activate its local offline JSON store without throwing errors.*

### 3. Build & Run Tests
To execute all 14 JUnit unit tests:
```bash
mvn clean test
```

### 4. Package Standalone Fat JAR
To assemble the single executable JAR with all dependencies:
```bash
mvn package -DskipTests
```
The output file is located at:
`target/student-productivity-predictor-1.0.0.jar` (~7.3 MB)

### 5. Launch Application
You can run the application using either of the following commands:

**Option A (Direct JAR execution):**
```bash
java -jar target/student-productivity-predictor-1.0.0.jar
```

**Option B (Maven Exec Plugin):**
```bash
mvn exec:java
```

---

## Machine Learning Benchmarks (16,000 Kaggle Records)

Evaluated on an **80/20 train/test split** (12,800 train records, 3,200 held-out test records) using random seed 42:

| Algorithm / Model | Model Family | MAE (Points) | RMSE (Points) | $R^2$ (Variance) | Max Residual |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Baseline (Mean)** | Baseline Reference | 13.3899 | 16.3890 | -0.0005 | 50.22 |
| **Ridge Linear Regression** | Regularized OLS | 0.0027 | 0.0032 | 1.0000 | 0.01 |
| **Decision Tree Regressor** | Tree-based Variance Reduction | 4.0628 | 5.0437 | 0.9052 | 18.00 |
| **Random Forest (Champion)** | Ensemble (25 Bagged Trees) | **2.2840** | **2.9045** | **0.9686** | **11.12** |

### Understanding the Metrics:
- **MAE (Mean Absolute Error)**: Average absolute point difference between predicted and actual productivity scores. Lower is better.
- **RMSE (Root Mean Squared Error)**: Penalizes larger point mistakes more heavily. Lower is better.
- **$R^2$ (Coefficient of Determination)**: Proportion of variance explained by input lifestyle features. A score of 0.9686 means the model captures ~96.9% of the variability.

---

## Application Navigation Guide

1. **Dashboard (Page 1)**: Visualizes overall dataset status, model training status, record counts, and the histogram distribution of productivity scores.
2. **Predict Productivity (Page 2)**: Form containing verified lifestyle features (study hours, sleep, screen time, coffee, attendance, etc.) with real-time validation, yielding a predicted productivity score, 95% confidence bounds, and cautious academic observations.
3. **Data Analysis (Page 3)**: Detailed descriptive statistics table (Min, Max, Mean, Std Dev, Median) and Pearson correlation bar chart.
4. **Model Performance (Page 4)**: Benchmark comparison table, metric explanations, and an interactive Actual vs. Predicted scatter plot.
5. **Prediction History (Page 5)**: Audit log of past predictions with keyword filtering, record deletion, CSV export, and live database status indicator.
6. **About Project (Page 6)**: Project overview, objectives, technology stack, dataset citations, limitations, future scope, and team member details.

---

## Troubleshooting

- **Error: "Dataset file data/train.csv not found"**:
  Ensure you are running commands from the root `APP_PROJECT` directory, or use the "Load / Change Dataset CSV" button on the Dashboard.
- **Error: "Communications link failure" with MySQL**:
  Ensure MySQL 8 service is running on port 3306. If MySQL is not installed, no action is needed—the application will display `Offline (Local JSON Storage Active)` and continue to function seamlessly.
- **Display or DPI scaling issues on High-DPI screens**:
  The application uses FlatLaf, which natively scales for High-DPI displays. If needed, launch with:
  ```bash
  java -Dsun.java2d.uiScale=1.0 -jar target/student-productivity-predictor-1.0.0.jar
  ```
