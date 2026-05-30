package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorSearchRequest {

    @JsonProperty("space_id")
    private Long spaceId;

    private String question;

    @JsonProperty("top_k")
    private Integer topK;

    @JsonProperty("min_score")
    private Double minScore;

    public AiVectorSearchRequest() {
    }

    public AiVectorSearchRequest(Long spaceId, String question, Integer topK, Double minScore) {
        this.spaceId = spaceId;
        this.question = question;
        this.topK = topK;
        this.minScore = minScore;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public String getQuestion() {
        return question;
    }

    public Integer getTopK() {
        return topK;
    }

    public Double getMinScore() {
        return minScore;
    }
}