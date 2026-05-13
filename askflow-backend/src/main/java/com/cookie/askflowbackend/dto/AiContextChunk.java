package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiContextChunk {

    @JsonProperty("chunk_id")
    private String chunkId;

    @JsonProperty("document_name")
    private String documentName;

    private String content;

    public AiContextChunk() {
    }

    public AiContextChunk(String chunkId, String documentName, String content) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
    }

    public String getChunkId() {
        return chunkId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getContent() {
        return content;
    }
}