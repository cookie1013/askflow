package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorSearchHit {

    @JsonProperty("chunk_id")
    private Long chunkId;

    @JsonProperty("space_id")
    private Long spaceId;

    @JsonProperty("document_id")
    private Long documentId;

    @JsonProperty("document_title")
    private String documentTitle;

    @JsonProperty("chunk_index")
    private Integer chunkIndex;

    @JsonProperty("token_count")
    private Integer tokenCount;

    private String content;

    private Double score;

    public Long getChunkId() {
        return chunkId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public String getContent() {
        return content;
    }

    public Double getScore() {
        return score;
    }
}