package com.mumbai.ml;

import java.util.*;

public class LinearRegressionEngine {

    private double slope, intercept, rSquared;
    private List<Double> xVals = new ArrayList<>();
    private List<Double> yVals = new ArrayList<>();

    public void train(List<Double> values) {
        if (values.size() < 2) throw new IllegalArgumentException("Need at least 2 semesters of data.");
        xVals.clear(); yVals.clear();
        for (int i = 0; i < values.size(); i++) {
            xVals.add((double)(i + 1));
            yVals.add(values.get(i));
        }
        calcRegression();
    }

    private void calcRegression() {
        int n = xVals.size();
        double mx = xVals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double my = yVals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            num += (xVals.get(i) - mx) * (yVals.get(i) - my);
            den += Math.pow(xVals.get(i) - mx, 2);
        }
        slope = den != 0 ? num / den : 0;
        intercept = my - slope * mx;
        calcRSquared(my);
    }

    private void calcRSquared(double meanY) {
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < xVals.size(); i++) {
            ssRes += Math.pow(yVals.get(i) - predict(xVals.get(i)), 2);
            ssTot += Math.pow(yVals.get(i) - meanY, 2);
        }
        rSquared = ssTot != 0 ? 1 - (ssRes / ssTot) : 0;
    }

    public double predictNext()          { return predict(xVals.size() + 1); }
    public double predict(double x)      { return slope * x + intercept; }
    public double getConfidence()        { return Math.max(0, Math.min(100, rSquared * 100)); }
    public double getSlope()             { return slope; }

    public String getTrend() {
        if      (slope >  0.5)  return "IMPROVING";
        else if (slope >  0.1)  return "SLIGHTLY_IMPROVING";
        else if (slope < -0.5)  return "DECLINING";
        else if (slope < -0.1)  return "SLIGHTLY_DECLINING";
        else                    return "STABLE";
    }
}
