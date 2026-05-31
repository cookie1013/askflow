package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiContextChunk {

    @JsonProperty("chunk_id")
    private String chunkId;

    @JsonProperty("document_name")
    private String documentName;

    private String content;

    private Double score;

    public AiContextChunk() {
    }

    public AiContextChunk(String chunkId, String documentName, String content) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
    }

    public AiContextChunk(String chunkId, String documentName, String content, Double score) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
        this.score = score;
    }

    @JsonProperty("chunk_id")
    public String getChunkId() {
        return chunkId;
    }

    @JsonProperty("document_name")
    public String getDocumentName() {
        return documentName;
    }

    public String getContent() {
        return content;
    }

    public Double getScore() {
        return score;
    }
}