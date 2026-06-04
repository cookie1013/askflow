package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class RagTraceListItemResponse {

    private Long id;
    private Long spaceId;
    private Long sessionId;
    private String question;
    private String retrievalMode;
    private Integer topK;
    private Double minScore;
    private Integer contextCount;
    private Long retrievalTimeMs;
    private Long generationTimeMs;
    private Long totalTimeMs;
    private Boolean refused;
    private String status;
    private LocalDateTime createdAt;

    public RagTraceListItemResponse(Long id,
                                    Long spaceId,
                                    Long sessionId,
                                    String question,
                                    String retrievalMode,
                                    Integer topK,
                                    Double minScore,
                                    Integer contextCount,
                                    Long retrievalTimeMs,
                                    Long generationTimeMs,
                                    Long totalTimeMs,
                                    Boolean refused,
                                    String status,
                                    LocalDateTime createdAt) {
        this.id = id;
        this.spaceId = spaceId;
        this.sessionId = sessionId;
        this.question = question;
        this.retrievalMode = retrievalMode;
        this.topK = topK;
        this.minScore = minScore;
        this.contextCount = contextCount;
        this.retrievalTimeMs = retrievalTimeMs;
        this.generationTimeMs = generationTimeMs;
        this.totalTimeMs = totalTimeMs;
        this.refused = refused;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getQuestion() {
        return question;
    }

    public String getRetrievalMode() {
        return retrievalMode;
    }

    public Integer getTopK() {
        return topK;
    }

    public Double getMinScore() {
        return minScore;
    }

    public Integer getContextCount() {
        return contextCount;
    }

    public Long getRetrievalTimeMs() {
        return retrievalTimeMs;
    }

    public Long getGenerationTimeMs() {
        return generationTimeMs;
    }

    public Long getTotalTimeMs() {
        return totalTimeMs;
    }

    public Boolean getRefused() {
        return refused;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}