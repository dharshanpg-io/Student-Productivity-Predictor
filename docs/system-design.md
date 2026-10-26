# System Design & Architecture: Student Productivity Predictor

## 1. Architectural Overview
The application follows a modular, tiered architectural design pattern implemented strictly in **Java 17**:

```
+---------------------------------------------------------------------------------+
|                                USER INTERFACE LAYER                             |
|       (Java Swing + FlatLaf Modern Theme + JFreeChart Interactive Visuals)      |
|  [Dashboard]  [Predictor]  [Data Analysis]  [Performance]  [History]  [About]   |
+---------------------------------------------------------------------------------+
                                      |
                                      v
+---------------------------------------------------------------------------------+
|                                SERVICE COORDINATOR                              |
|                          (ProductivityService.java)                             |
|       - Dataset Orchestration        - Inference Engine Coordination            |
|       - Model Benchmark Training     - Insights & Confidence Calculations       |
+---------------------------------------------------------------------------------+
           |                                                |
           v                                                v
+-------------------------------+       +-----------------------------------------+
|     DATA & ML ENGINE          |       |           PERSISTENCE LAYER             |
|  - CsvLoader                  |       |  - DatabaseManager (MySQL 8 via JDBC)   |
|  - DataPreprocessor (Z-Score) |       |  - PredictionHistoryDao                 |
|  - BaselineMeanModel          |       |  - LocalHistoryStore (JSON Fallback)    |
|  - LinearRegression (Ridge)   |       +-----------------------------------------+
|  - DecisionTreeRegressor      |
|  - RandomForestRegressor      |
+-------------------------------+
```

## 2. Component Specifications

### 2.1 Data Processing Layer (`com.studentproductivity.data`)
- **`StudentRecord`**: Immutable POJO representing individual student observations.
- **`Dataset`**: Encapsulates records, schema metadata, descriptive statistics (min, max, mean, standard deviation, median), and Pearson correlation coefficients with the target.
- **`CsvLoader`**: Parses RFC 4180-compliant CSV files, validates column integrity, filters duplicate records by ID, detects range anomalies, and outputs an audit report.
- **`DataPreprocessor`**: Executes one-hot encoding on categorical attributes (`gender`: Male, Female, Other) and computes Z-score standardization:
  $$z_j = \frac{x_j - \mu_j}{\sigma_j}$$
  fitted strictly on the training partition to prevent information leakage.

### 2.2 Machine Learning Engine (`com.studentproductivity.model`)
- **`MLModel` Interface**: Standard contract defining `train()`, `predict()`, `getFeatureImportances()`, and model status inspection.
- **`BaselineMeanModel`**: Benchmarking standard predicting the training target mean $\bar{y}$.
- **`LinearRegressionModel`**: Multiple Linear Regression with Ridge regularization (L2) solved via Gaussian elimination with partial pivoting:
  $$(X^T X + \lambda I)\beta = X^T y$$
- **`DecisionTreeRegressor`**: Recursive binary regression tree splitting nodes by maximizing variance reduction:
  $$\Delta \text{Var} = \text{Var}(S) - \frac{|S_L|}{|S|}\text{Var}(S_L) - \frac{|S_R|}{|S|}\text{Var}(S_R)$$
- **`RandomForestRegressor`**: Ensemble of randomized trees trained over bootstrap samples with feature sub-selection ($m \approx \sqrt{p}$) and parallel tree evaluation.
- **`ModelEvaluator`**: Computes standard regression metrics on held-out test records:
  $$\text{MAE} = \frac{1}{N}\sum_{i=1}^N |y_i - \hat{y}_i|$$
  $$\text{RMSE} = \sqrt{\frac{1}{N}\sum_{i=1}^N (y_i - \hat{y}_i)^2}$$
  $$R^2 = 1 - \frac{\sum_{i=1}^N (y_i - \hat{y}_i)^2}{\sum_{i=1}^N (y_i - \bar{y})^2}$$

### 2.3 Persistence Layer (`com.studentproductivity.database`)
- **`DatabaseManager`**: Configures connection to MySQL 8 with configurable timeout and automated schema initialization (`CREATE TABLE IF NOT EXISTS prediction_history`).
- **`LocalHistoryStore`**: JSON-backed local cache ensuring continuous operation when MySQL is unreachable.
- **`PredictionHistoryDao`**: Unified DAO providing CRUD operations, keyword searching, and history export.

### 2.4 User Interface Layer (`com.studentproductivity.ui`)
- Built with **Java Swing** leveraging **FlatLaf Light** styling.
- Responsive layout with a persistent sidebar and top application banner.
- Asynchronous operation through `SwingWorker` threads to prevent UI freezing during model training or inference.
