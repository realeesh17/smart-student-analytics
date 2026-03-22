# 🎓 Smart Student Analytics
### Mumbai University Engineering Performance Predictor

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![Machine Learning](https://img.shields.io/badge/Machine%20Learning-Linear%20Regression-green?style=for-the-badge)

> A smart desktop application for Mumbai University B.E. students and teachers to track, analyze and predict academic performance using Machine Learning.

---

## 📸 Features

### 🏠 Dashboard
- Select any student and view their **semester-wise SGPA chart**
- Click **Predict Performance** to get ML-powered prediction for next semester
- See predicted CGPA, percentage, grade and class category

### 👨‍🏫 Teacher Dashboard
- Add new students with branch, semester and roll number
- Enter subject marks **(IA1 + IA2 + ESE + Practical)**
- Record attendance subject-wise
- Auto-calculates CGPA and percentage

### 🎓 Student Self-Check Portal
- Students enter their **Roll Number** to check their own performance
- See subject-wise marks with progress bars
- Get personalized AI recommendations
- View predicted performance for next semester

### 📊 Marks Report
- Complete semester-wise marks table
- Filter by student and semester
- View IA, ESE, Practical marks in one place

---

## 🏆 Mumbai University Grade System

| Marks (%) | Grade | Grade Point | Class |
|-----------|-------|-------------|-------|
| 80 - 100  | O     | 10.0        | Outstanding |
| 70 - 79   | A+    | 9.0         | First Class with Distinction |
| 60 - 69   | A     | 8.0         | First Class |
| 55 - 59   | B+    | 7.0         | First Class |
| 50 - 54   | B     | 6.0         | Second Class |
| 45 - 49   | C     | 5.0         | Second Class |
| 40 - 44   | D     | 4.0         | Pass Class |
| < 40      | F     | 0.0         | Fail |

---

## 🤖 How the ML Works

```
Linear Regression:  CGPA = m × Semester + b

where:
  m = slope (rate of improvement/decline per semester)
  b = base intercept
  
Confidence = R² × 100%  (how well the model fits past data)

Final prediction is adjusted by:
  • Attendance  (< 75% = detention risk warning!)
  • Assignment scores (weighted 20%)
  • Past SGPA trend
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Core Java  | Backend logic |
| JDBC       | Database connectivity |
| MySQL      | Data storage |
| JavaFX     | Desktop GUI |
| Linear Regression | ML prediction |

---

## ⚙️ Setup Instructions

### 1. Requirements
- Java JDK 21+
- MySQL 8.0+
- JavaFX SDK 21+
- Eclipse IDE

### 2. Database Setup
```bash
mysql -u root -p < sql/mumbai_uni_schema.sql
```

### 3. Configure Password
Open `src/com/mumbai/util/DBConnection.java`:
```java
private static final String PASSWORD = "your_password_here";
```

### 4. Eclipse Setup
- Create new Java Project
- Copy `src/com/mumbai` folder into project
- Add `mysql-connector-j-9.6.0.jar` to **Classpath**
- Add all JavaFX JARs to **Modulepath**

### 5. VM Arguments
```
--module-path "path/to/javafx-sdk/lib" 
--add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media,javafx.swing,javafx.web
```

### 6. Run
```
Right-click MainApp.java → Run As → Java Application
```

---

## 📁 Project Structure

```
smart-student-analytics/
├── src/
│   └── com/mumbai/
│       ├── util/       → DBConnection.java
│       ├── ml/         → LinearRegressionEngine.java
│       ├── model/      → Student.java, SubjectMark.java, PredictionResult.java
│       ├── dao/        → StudentDAO.java
│       ├── service/    → PerformancePredictor.java
│       └── ui/         → MainApp.java
├── sql/
│   └── mumbai_uni_schema.sql
└── README.md
```

---

## 👨‍💻 Developer

**Rakesh Babriya**  
Mumbai University Engineering Student  
GitHub: [@realeesh17](https://github.com/realeesh17)

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).

---

⭐ **If you found this helpful, please give it a star!** ⭐
