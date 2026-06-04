package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AiPdfParseResponse {

    private String filename;

    @JsonProperty("chunk_count")
    private Integer chunkCount;

    private List<AiPdfParseChunk> chunks;

    public String getFilename() {
        return filename;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public List<AiPdfParseChunk> getChunks() {
        return chunks;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setChunkCount(Integer chunkCount) {
        this.chunkCount = chunkCount;
    }

    public void setChunks(List<AiPdfParseChunk> chunks) {
        this.chunks = chunks;
    }
}