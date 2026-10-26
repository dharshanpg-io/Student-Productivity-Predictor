# Problem Statement: Student Productivity Predictor

## Academic Metadata
- **Project Title**: Student Productivity Predictor — Machine Learning-Driven Academic Analytics
- **Department**: Computer Science and Engineering (Artificial Intelligence and Machine Learning)
- **Student Team**:
  - **P.G. DHARSHAN** — Register Number: **RA2511026050012**
  - **NITHISH RAJ A** — Register Number: **RA2511026050034**

---

## 1. Context & Background
In modern higher education, students navigate multifaceted schedules characterized by rigorous coursework, varied sleep routines, digital device usage, extracurriculars, and personal commitments. A recurring challenge observed across engineering disciplines is the lack of objective feedback regarding how daily lifestyle and study behaviors translate into academic effectiveness and productivity.

Students frequently experience cognitive overload, erratic study routines, and distraction from personal electronic devices. While numerous mobile tracking applications record isolated metrics (such as screen time or sleep duration), they seldom synthesize these disparate variables into an integrated predictive assessment that highlights actionable habit patterns.

## 2. Formal Problem Statement
Given a multidimensional feature vector $X \in \mathbb{R}^p$ representing student demographic, lifestyle, and academic habits (including study duration, sleep duration, mobile device distractions, attendance rate, stress index, and focus scores), the objective is to learn a mapping:

$$f: X \rightarrow y \in [0, 100]$$

where $y$ represents a continuous **Productivity Score**, evaluated through supervised machine learning models trained strictly in **Java** on authentic student cohort records downloaded from Kaggle, while providing personalized, non-deterministic educational insights without Python runtime dependencies.

## 3. Key Challenges Addressed
1. **Multi-Factor Interaction**: Understanding the joint effects of sleep, study hours, and digital distractions rather than analyzing variables in isolation.
2. **Pure Java ML Implementation**: Eliminating reliance on Python-based ML microservices by implementing robust regression algorithms (Ridge Regression, Decision Trees, and Random Forests) directly within the Java 17 LTS runtime.
3. **Data Integrity & Leakage Prevention**: Enforcing strict separation between training and test sets, ensuring scaling parameters are fitted solely on training splits.
4. **Resilient Persistence**: Ensuring full application usability whether a MySQL 8 server is active or offline, through automated fallback to local JSON audit logs.
5. **Ethical Academic Communication**: Ensuring predictions are clearly presented as statistical approximations rather than deterministic judgments of human intelligence or medical diagnostics.
