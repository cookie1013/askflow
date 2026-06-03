package com.cookie.askflowbackend.dto;

public class RagEvalSummaryResponse {

    private long total;
    private long passed;
    private double passRate;
    private double avgTotalTimeMs;

    public RagEvalSummaryResponse(long total, long passed, double passRate, double avgTotalTimeMs) {
        this.total = total;
        this.passed = passed;
        this.passRate = passRate;
        this.avgTotalTimeMs = avgTotalTimeMs;
    }

    public long getTotal() {
        return total;
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