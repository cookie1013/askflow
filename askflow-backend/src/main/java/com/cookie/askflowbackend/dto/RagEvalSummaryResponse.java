package com.cookie.askflowbackend.dto;

public class RagEvalSummaryResponse {

    private long caseTotal;
    private long evaluated;
    private long passed;
    private double passRate;

    private double recallAtK;
    private double mrr;
    private double citationAccuracy;
    private double refusalAccuracy;
    private double misRecallRate;
    private double answerKeywordAccuracy;

    private double avgTotalTimeMs;

    public RagEvalSummaryResponse(long caseTotal,
                                  long evaluated,
                                  long passed,
                                  double passRate,
                                  double recallAtK,
                                  double mrr,
                                  double citationAccuracy,
                                  double refusalAccuracy,
                                  double misRecallRate,
                                  double answerKeywordAccuracy,
                                  double avgTotalTimeMs) {
        this.caseTotal = caseTotal;
        this.evaluated = evaluated;
        this.passed = passed;
        this.passRate = passRate;
        this.recallAtK = recallAtK;
        this.mrr = mrr;
        this.citationAccuracy = citationAccuracy;
        this.refusalAccuracy = refusalAccuracy;
        this.misRecallRate = misRecallRate;
        this.answerKeywordAccuracy = answerKeywordAccuracy;
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

    public double getRecallAtK() {
        return recallAtK;
    }

    public double getMrr() {
        return mrr;
    }

    public double getCitationAccuracy() {
        return citationAccuracy;
    }

    public double getRefusalAccuracy() {
        return refusalAccuracy;
    }

    public double getMisRecallRate() {
        return misRecallRate;
    }

    public double getAnswerKeywordAccuracy() {
        return answerKeywordAccuracy;
    }

    public double getAvgTotalTimeMs() {
        return avgTotalTimeMs;
    }
}