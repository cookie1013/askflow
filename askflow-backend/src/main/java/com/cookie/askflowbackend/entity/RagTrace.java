package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rag_trace")
public class RagTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long spaceId;

    private Long sessionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String rewrittenQuery;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    @Column(nullable = false, length = 50)
    private String retrievalMode;

    @Column(name = "top_k", nullable = false)
    private Integer topK;

    @Column(nullable = false)
    private Double minScore;

    @Column(nullable = false)
    private Integer contextCount;

    @Column(columnDefinition = "JSON")
    private String retrievalScoresJson;

    @Column(columnDefinition = "JSON")
    private String citationsJson;

    private Long retrievalTimeMs;

    private Long generationTimeMs;

    private Long totalTimeMs;

    @Column(nullable = false)
    private Boolean refused = false;

    @Column(nullable = false, length = 30)
    private String status = "SUCCESS";

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRewrittenQuery() {
        return rewrittenQuery;
    }

    public void setRewrittenQuery(String rewrittenQuery) {
        this.rewrittenQuery = rewrittenQuery;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRetrievalMode() {
        return retrievalMode;
    }

    public void setRetrievalMode(String retrievalMode) {
        this.retrievalMode = retrievalMode;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Integer getContextCount() {
        return contextCount;
    }

    public void setContextCount(Integer contextCount) {
        this.contextCount = contextCount;
    }

    public String getRetrievalScoresJson() {
        return retrievalScoresJson;
    }

    public void setRetrievalScoresJson(String retrievalScoresJson) {
        this.retrievalScoresJson = retrievalScoresJson;
    }

    public String getCitationsJson() {
        return citationsJson;
    }

    public void setCitationsJson(String citationsJson) {
        this.citationsJson = citationsJson;
    }

    public Long getRetrievalTimeMs() {
        return retrievalTimeMs;
    }

    public void setRetrievalTimeMs(Long retrievalTimeMs) {
        this.retrievalTimeMs = retrievalTimeMs;
    }

    public Long getGenerationTimeMs() {
        return generationTimeMs;
    }

    public void setGenerationTimeMs(Long generationTimeMs) {
        this.generationTimeMs = generationTimeMs;
    }

    public Long getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(Long totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }

    public Boolean getRefused() {
        return refused;
    }

    public void setRefused(Boolean refused) {
        this.refused = refused;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}