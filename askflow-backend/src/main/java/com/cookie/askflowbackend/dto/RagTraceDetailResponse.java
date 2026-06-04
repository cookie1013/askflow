package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RagTraceDetailResponse {

    private Long id;
    private Long spaceId;
    private Long sessionId;
    private String question;
    private String rewrittenQuery;
    private String answer;

    private String retrievalMode;
    private Integer topK;
    private Double minScore;
    private Integer contextCount;

    private List<Map<String, Object>> retrievalScores;
    private List<Map<String, Object>> citations;
    private List<RagTraceChunkResponse> chunks;

    private Long retrievalTimeMs;
    private Long generationTimeMs;
    private Long totalTimeMs;

    private Boolean refused;
    private String status;
    private String errorMessage;
    private LocalDateTime createdAt;

    public RagTraceDetailResponse(Long id,
                                  Long spaceId,
                                  Long sessionId,
                                  String question,
                                  String rewrittenQuery,
                                  String answer,
                                  String retrievalMode,
                                  Integer topK,
                                  Double minScore,
                                  Integer contextCount,
                                  List<Map<String, Object>> retrievalScores,
                                  List<Map<String, Object>> citations,
                                  List<RagTraceChunkResponse> chunks,
                                  Long retrievalTimeMs,
                                  Long generationTimeMs,
                                  Long totalTimeMs,
                                  Boolean refused,
                                  String status,
                                  String errorMessage,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.spaceId = spaceId;
        this.sessionId = sessionId;
        this.question = question;
        this.rewrittenQuery = rewrittenQuery;
        this.answer = answer;
        this.retrievalMode = retrievalMode;
        this.topK = topK;
        this.minScore = minScore;
        this.contextCount = contextCount;
        this.retrievalScores = retrievalScores;
        this.citations = citations;
        this.chunks = chunks;
        this.retrievalTimeMs = retrievalTimeMs;
        this.generationTimeMs = generationTimeMs;
        this.totalTimeMs = totalTimeMs;
        this.refused = refused;
        this.status = status;
        this.errorMessage = errorMessage;
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

    public String getRewrittenQuery() {
        return rewrittenQuery;
    }

    public String getAnswer() {
        return answer;
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

    public List<Map<String, Object>> getRetrievalScores() {
        return retrievalScores;
    }

    public List<Map<String, Object>> getCitations() {
        return citations;
    }

    public List<RagTraceChunkResponse> getChunks() {
        return chunks;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}