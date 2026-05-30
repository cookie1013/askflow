package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AiRagAskRequest {

    private String question;

    @JsonProperty("context_chunks")
    private List<AiContextChunk> contextChunks;

    public AiRagAskRequest() {
    }

    public AiRagAskRequest(String question, List<AiContextChunk> contextChunks) {
        this.question = question;
        this.contextChunks = contextChunks;
    }

    public String getQuestion() {
        return question;
    }

    @JsonProperty("context_chunks")
    public List<AiContextChunk> getContextChunks() {
        return contextChunks;
    }
}