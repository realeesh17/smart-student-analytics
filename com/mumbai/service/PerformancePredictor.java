package com.mumbai.service;

import com.mumbai.ml.LinearRegressionEngine;
import com.mumbai.model.PredictionResult;
import java.util.List;

public class PerformancePredictor {

    private final LinearRegressionEngine engine = new LinearRegressionEngine();

    public PredictionResult predict(List<Double> sgpaPerSem, double attendance, double assignmentScore) {

        engine.train(sgpaPerSem);

        double predictedCgpa = engine.predictNext();
        predictedCgpa = adjust(predictedCgpa, attendance, assignmentScore);
        predictedCgpa = Math.min(10.0, Math.max(0.0, predictedCgpa));

        double predictedPct = cgpaToPercentage(predictedCgpa);

        PredictionResult r = new PredictionResult();
        r.setPredictedCgpa(predictedCgpa);
        r.setPredictedPercentage(predictedPct);
        r.setConfidence(engine.getConfidence());
        r.setTrend(engine.getTrend());
        r.setGradeCategory(PredictionResult.getClassCategory(predictedCgpa));
        r.setRecommendation(buildRec(predictedCgpa, engine.getTrend(), attendance));
        return r;
    }

    private double adjust(double base, double att, double assignment) {
        double score = base;
        if (att < 75)       score -= (75 - att) * 0.02;
        else if (att > 90)  score += 0.2;
        double assignGp = assignment / 10.0;
        score = score * 0.8 + assignGp * 0.2;
        return score;
    }

    /** Approximate Mumbai Uni CGPA → percentage */
    public static double cgpaToPercentage(double cgpa) {
        return (cgpa / 10.0) * 100.0;
    }

    private String buildRec(double cgpa, String trend, double att) {
        StringBuilder sb = new StringBuilder();

        if      (cgpa >= 9.0) sb.append("🏆 Outstanding performance! Aim for research/higher studies.\n");
        else if (cgpa >= 7.5) sb.append("🥇 Excellent! You are on track for First Class with Distinction.\n");
        else if (cgpa >= 6.0) sb.append("🥈 Good work! Push harder to reach Distinction level.\n");
        else if (cgpa >= 5.0) sb.append("🥉 Second Class. Focus more on ESE preparation.\n");
        else if (cgpa >= 4.0) sb.append("⚠️ Just passing. Immediate improvement needed.\n");
        else                  sb.append("🚨 At risk of failing! Seek help immediately.\n");

        switch (trend) {
            case "IMPROVING":          sb.append("📈 Great trend — keep the momentum!\n"); break;
            case "SLIGHTLY_IMPROVING": sb.append("📊 Slight improvement. Push harder.\n"); break;
            case "DECLINING":          sb.append("📉 Performance declining. Review your strategy.\n"); break;
            case "SLIGHTLY_DECLINING": sb.append("⚠️ Slight dip. Stay consistent.\n"); break;
            default:                   sb.append("➡️ Stable performance. Push for improvement.\n");
        }

        if (att < 75)  sb.append("🚨 Attendance below 75% — you may be DETAINED from exams!\n");
        else if (att < 85) sb.append("📚 Improve attendance above 85% for better results.\n");
        else           sb.append("✅ Good attendance — keep it up!\n");

        sb.append("\n💡 Tips:\n");
        sb.append("• Focus on Internal Assessment (IA) — easy marks!\n");
        sb.append("• Solve previous year Mumbai University papers.\n");
        sb.append("• Form study groups for tough subjects.\n");

        return sb.toString().trim();
    }
}
