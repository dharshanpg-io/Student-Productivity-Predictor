# Testing Methodology & Verification Report

## Academic Project Details
- **Project Title**: Student Productivity Predictor
- **Team**: P.G. DHARSHAN (RA2511026050012) & NITHISH RAJ A (RA2511026050034)
- **Testing Framework**: JUnit 5 (JUnit Jupiter 5.10.2)
- **Build Tool**: Apache Maven 3.9.6
- **Test Execution Environment**: OpenJDK 17.0.12 LTS on Windows 11 x64

---

## 1. Test Suite Coverage Summary

| Test Class | Category | Test Cases Executed | Result |
| :--- | :--- | :--- | :--- |
| `CsvLoaderTest` | Data Ingestion | 3 | PASSED |
| `DataPreprocessorTest` | Feature Pipeline | 2 | PASSED |
| `MLModelTest` | Model & Evaluator | 4 | PASSED |
| `PredictionRequestTest` | Input Validation | 3 | PASSED |
| `PredictionHistoryTest` | Persistence & DAO | 1 | PASSED |
| `InsightGeneratorTest` | Insights & Ethics | 1 | PASSED |
| **Total** | **All Core Modules** | **14** | **14 PASSED (100%)** |

---

## 2. Detailed Test Specifications

### 2.1 CSV Loading & Schema Validation (`CsvLoaderTest`)
- **`testLoadValidCsv`**: Verifies that standard CSV rows parse correctly into `StudentRecord` objects with valid types and target variables.
- **`testDuplicateIdDetection`**: Validates that records with duplicate primary IDs are detected, skipped, and logged in the loading audit report without dataset corruption.
- **`testMissingEssentialColumnThrows`**: Asserts that malformed CSV files missing essential features fail with informative `IOException` messages rather than silent crashes.

### 2.2 Feature Preprocessing & Splitting (`DataPreprocessorTest`)
- **`testExtractRawFeaturesAndOneHotEncoding`**: Confirms that categorical attributes (`gender`: Male, Female, Other) are converted into distinct one-hot binary flags ($[1,0,0]$, $[0,1,0]$, $[0,0,1]$).
- **`testTrainTestSplitReproducibility`**: Confirms that setting random seed 42 produces deterministic, reproducible splits with zero partition discrepancy.

### 2.3 Input Validation (`PredictionRequestTest`)
- **`testValidPredictionRequest`**: Verifies acceptance of standard realistic student parameters.
- **`testExceeding24HoursDayThrowsError`**: Validates business logic preventing physically impossible combinations (e.g. Study 18 hrs + Sleep 10 hrs = 28 hrs > 24 hrs in a day).
- **`testInvalidStressLevelThrowsError`**: Enforces strict domain range bounds (1 to 10 scale for stress level).

### 2.4 Machine Learning & Evaluator (`MLModelTest`)
- **`testBaselineModelPredictsMean`**: Confirms that the baseline benchmark produces the exact mathematical target mean.
- **`testLinearRegressionModelFit`**: Evaluates solver accuracy on synthetic linear functions ($y = 2x + 10$).
- **`testRandomForestModelTrainAndPredict`**: Verifies ensemble training, tree convergence, and prediction bounds ($0 \le \hat{y} \le 100$).
- **`testModelEvaluatorMetrics`**: Validates mathematical accuracy of MAE, RMSE, and $R^2$ calculations against known synthetic discrepancies.

### 2.5 Persistence & Insights (`PredictionHistoryTest`, `InsightGeneratorTest`)
- **`testLocalHistoryStorePersistenceAndFilter`**: Confirms CRUD, search filtering, and deletion over local JSON history.
- **`testInsightGeneratorContainsNoMedicalClaims`**: Verifies that generated insights enforce academic disclaimers and do not issue medical diagnoses or deterministic claims.

---

## 3. Actual Maven Test Execution Log
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.studentproductivity.CsvLoaderTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.studentproductivity.DataPreprocessorTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.studentproductivity.InsightGeneratorTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.studentproductivity.MLModelTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.studentproductivity.PredictionHistoryTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.studentproductivity.PredictionRequestTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
