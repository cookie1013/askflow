package com.cookie.askflowbackend.dto;

public class RagEvalSummaryResponse {

    private long caseTotal;
    private long evaluated;
    private long passed;
    private double passRate;
    private double avgTotalTimeMs;

    public RagEvalSummaryResponse(long caseTotal,
                                  long evaluated,
                                  long passed,
                                  double passRate,
                                  double avgTotalTimeMs) {
        this.caseTotal = caseTotal;
        this.evaluated = evaluated;
        this.passed = passed;
        this.passRate = passRate;
        this.avgTotalTimeMs = avgTotalTimeMs;
    }

    public long getCaseTotal() {
        return caseTotal;
    }

    public long getEvaluated() {
        return evaluated;
    }

    public long getPassed() {
        return passed;
    }

    public double getPassRate() {
        return passRate;
    }

    public double getAvgTotalTimeMs() {
        return avgTotalTimeMs;
    }
}