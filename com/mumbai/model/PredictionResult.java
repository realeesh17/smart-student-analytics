package com.mumbai.model;

public class PredictionResult {
    private double predictedCgpa;
    private double predictedPercentage;
    private double confidence;
    private String trend;
    private String recommendation;
    private String gradeCategory; // Distinction, First Class, etc.

    public double getPredictedCgpa()                      { return predictedCgpa; }
    public void   setPredictedCgpa(double v)              { this.predictedCgpa = v; }
    public double getPredictedPercentage()                { return predictedPercentage; }
    public void   setPredictedPercentage(double v)        { this.predictedPercentage = v; }
    public double getConfidence()                         { return confidence; }
    public void   setConfidence(double v)                 { this.confidence = v; }
    public String getTrend()                              { return trend; }
    public void   setTrend(String v)                      { this.trend = v; }
    public String getRecommendation()                     { return recommendation; }
    public void   setRecommendation(String v)             { this.recommendation = v; }
    public String getGradeCategory()                      { return gradeCategory; }
    public void   setGradeCategory(String v)              { this.gradeCategory = v; }

    /** Convert percentage to Mumbai University grade */
    public static String getGrade(double percentage) {
        if (percentage >= 80) return "O (Outstanding)";
        if (percentage >= 70) return "A+ (Excellent)";
        if (percentage >= 60) return "A (Very Good)";
        if (percentage >= 55) return "B+ (Good)";
        if (percentage >= 50) return "B (Above Average)";
        if (percentage >= 45) return "C (Average)";
        if (percentage >= 40) return "D (Pass)";
        return "F (Fail)";
    }

    /** Convert percentage to grade point (out of 10) */
    public static double getGradePoint(double percentage) {
        if (percentage >= 80) return 10.0;
        if (percentage >= 70) return 9.0;
        if (percentage >= 60) return 8.0;
        if (percentage >= 55) return 7.0;
        if (percentage >= 50) return 6.0;
        if (percentage >= 45) return 5.0;
        if (percentage >= 40) return 4.0;
        return 0.0;
    }

    /** Mumbai University class category */
    public static String getClassCategory(double cgpa) {
        if (cgpa >= 9.0)  return "🏆 Outstanding (O)";
        if (cgpa >= 7.5)  return "🥇 First Class with Distinction";
        if (cgpa >= 6.0)  return "🥈 First Class";
        if (cgpa >= 5.0)  return "🥉 Second Class";
        if (cgpa >= 4.0)  return "✅ Pass Class";
        return "❌ Fail";
    }
}
