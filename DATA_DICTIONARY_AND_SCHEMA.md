# MySQL Schema & Data Dictionary

## Project: Student Lifestyle & Academic Performance Analytics
**Target Database:** `student_analytics`  
**Table Name:** `students`  
**Engine:** `InnoDB`  
**Character Set:** `utf8mb4`  

---

## 1. MySQL DDL Script

```sql
-- =============================================================
-- MySQL Database DDL Script for Student Analytics System
-- Project: Student Lifestyle & Academic Performance Analytics
-- Database: student_analytics
-- =============================================================

CREATE DATABASE IF NOT EXISTS student_analytics;
USE student_analytics;

DROP TABLE IF EXISTS students;

CREATE TABLE students (
    student_id             VARCHAR(20)      PRIMARY KEY,
    name                   VARCHAR(100)     NOT NULL,
    study_hours            DOUBLE           NOT NULL,
    sleep_hours            DOUBLE           NOT NULL,
    attendance             DOUBLE           NOT NULL,
    assignment_completion  DOUBLE           NOT NULL,
    screen_time            DOUBLE           NOT NULL,
    exercise_hours         DOUBLE           NOT NULL,
    previous_marks         DOUBLE           NOT NULL,
    current_marks          DOUBLE           NOT NULL,
    performance_category   VARCHAR(15)      NOT NULL DEFAULT 'MEDIUM',
    is_anomaly             BOOLEAN          DEFAULT FALSE,
    anomaly_details        TEXT,
    created_at             TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_perf_category (performance_category),
    INDEX idx_current_marks (current_marks),
    INDEX idx_anomaly (is_anomaly)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 2. Comprehensive Data Dictionary

| # | Field Name in DB | CSV / UI Header | SQL Type | Java Type | Valid Range / Rule | Custom Exception Thrown on Violation | Analytical Description |
|---|---|---|---|---|---|---|---|
| **1** | `student_id` | `student_id` | `VARCHAR(20)` | `String` | Non-empty, alphanumeric / hyphens (e.g. `STU1001`) | `MissingFieldException` | Unique student identification key (Primary Key). |
| **2** | `name` | `name` | `VARCHAR(100)` | `String` | Non-empty string, 1 to 100 characters | `MissingFieldException` | Full name of the student. |
| **3** | `study_hours` | `study_hours` | `DOUBLE` | `double` | `[0.0, 24.0]` hours/day | `OutOfRangeException`, `MalformedRowException` | Average self-study hours per day outside regular class. |
| **4** | `sleep_hours` | `sleep_hours` | `DOUBLE` | `double` | `[0.0, 24.0]` hours/day | `OutOfRangeException`, `MalformedRowException` | Average hours of sleep per 24-hour cycle. |
| **5** | `attendance` | `attendance` | `DOUBLE` | `double` | `[0.0, 100.0]` percentage | `OutOfRangeException`, `MalformedRowException` | Class lecture attendance percentage. |
| **6** | `assignment_completion` | `assignment_completion` | `DOUBLE` | `double` | `[0.0, 100.0]` percentage | `OutOfRangeException`, `MalformedRowException` | Percentage of homework and laboratory assignments submitted. |
| **7** | `screen_time` | `screen_time` | `DOUBLE` | `double` | `[0.0, 24.0]` hours/day | `OutOfRangeException`, `MalformedRowException` | Daily recreational / leisure screen time (phones, gaming, social media). |
| **8** | `exercise_hours` | `exercise` | `DOUBLE` | `double` | `[0.0, 24.0]` hours/day | `OutOfRangeException`, `MalformedRowException` | Physical exercise, sports, or gym workout hours per day. |
| **9** | `previous_marks` | `previous_marks` | `DOUBLE` | `double` | `[0.0, 100.0]` percentage/score | `OutOfRangeException`, `MalformedRowException` | Academic score or aggregate percentage from the preceding semester/term. |
| **10** | `current_marks` | `current_marks` | `DOUBLE` | `double` | `[0.0, 100.0]` percentage/score | `OutOfRangeException`, `MalformedRowException` | Academic score or aggregate percentage in the current semester/term. |
| **11** | `performance_category` | `category` | `VARCHAR(15)` | `PerformanceCategory` (Enum) | `'HIGH'`, `'MEDIUM'`, `'LOW'` | N/A (Derived by `Classifier`) | Rule-based performance tier evaluated against current marks (default: High ≥ 75, Low < 50). |
| **12** | `is_anomaly` | `is_anomaly` | `BOOLEAN` | `boolean` | `true` or `false` | N/A (Derived by `AnalyticsEngine`) | Flagged if student's attributes deviate ≥ threshold standard deviations from class norms or exhibit paradoxes. |
| **13** | `anomaly_details` | `anomaly_details` | `TEXT` | `String` | String summary | N/A (Derived by `AnalyticsEngine`) | Plain-language explanation of detected anomaly (e.g. `Deviates on: Study Hours (10.5h, z=2.45)`). |
| **14** | `created_at` | N/A | `TIMESTAMP` | `java.sql.Timestamp` | Database generated timestamp | N/A | Audit timestamp recording when the record was persisted into MySQL. |

---

## 3. Data Validation & Boundary Matrix

| Metric | Minimum Allowed | Maximum Allowed | Expected Class Norm (Typical) | Boundary Rationale |
|---|---|---|---|---|
| **Study Hours** | `0.0` | `24.0` | `2.0 - 6.0` hrs | A day has 24 hours. Values `< 0.0` or `> 24.0` are physically impossible. |
| **Sleep Hours** | `0.0` | `24.0` | `6.0 - 8.5` hrs | Values `< 0.0` or `> 24.0` are physically impossible. Extreme values (< 4 or > 11) trigger anomaly alerts. |
| **Attendance** | `0.0%` | `100.0%` | `70.0% - 95.0%` | Attendance is a percentage ratio; cannot exceed 100% or be negative. |
| **Assignment Completion** | `0.0%` | `100.0%` | `75.0% - 100.0%` | Assignment completion is bounded between 0% and 100%. |
| **Screen Time** | `0.0` | `24.0` | `2.0 - 6.0` hrs | Recreational screen time cannot exceed total hours in a day. |
| **Exercise Hours** | `0.0` | `24.0` | `0.5 - 2.0` hrs | Physical workout duration per day bounded by 24 hours. |
| **Previous Marks** | `0.0` | `100.0` | `40.0 - 90.0` | Standard educational percentage grading scale `[0, 100]`. |
| **Current Marks** | `0.0` | `100.0` | `40.0 - 90.0` | Standard educational percentage grading scale `[0, 100]`. |

---

## 4. Derived & Analytical Attributes

### A. Performance Categorization Rules
The system uses the `Classifier` functional interface and `RuleBasedClassifier` implementation:
- **`HIGH` Performer:** `current_marks >= 75.0`
- **`MEDIUM` Performer:** `50.0 <= current_marks < 75.0`
- **`LOW` Performer:** `current_marks < 50.0`
*(Thresholds are user-configurable at runtime).*

### B. Anomaly Detection Criteria
A student is flagged as `is_anomaly = true` if:
1. **Statistical Outlier:** Any lifestyle or academic metric yields a Z-score:
   $$|Z| = \left|\frac{X - \mu}{\sigma}\right| \ge \text{threshold (default 2.0)}$$
2. **Domain-Logic Paradox:** For example:
   - High study hours ($\ge 9.0$ hrs/day) but Low current marks ($< 45.0$).
   - Very low attendance ($< 40\%$) but High current marks ($\ge 85.0$).

### C. Scientific Disclaimer Requirement
All analytical reports and correlation views **must display**:
> **CAVEAT:** *Correlation does not imply causation. A statistically strong correlation between variables (e.g., study hours and current marks) indicates co-occurrence or association within this dataset, not a direct causal connection. Other confounding variables (e.g., prior knowledge, teaching quality, self-discipline) may influence both metrics.*
