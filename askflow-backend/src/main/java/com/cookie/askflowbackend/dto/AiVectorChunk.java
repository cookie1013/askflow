package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorChunk {

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

    private String content;

    @JsonProperty("token_count")
    private Integer tokenCount;

    public AiVectorChunk() {
    }

    public AiVectorChunk(Long chunkId, Long spaceId, Long documentId, String documentTitle,
                         Integer chunkIndex, String content, Integer tokenCount) {
        this.chunkId = chunkId;
        this.spaceId = spaceId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.tokenCount = tokenCount;
    }

    @JsonProperty("chunk_id")
    public Long getChunkId() {
        return chunkId;
    }

    @JsonProperty("space_id")
    public Long getSpaceId() {
        return spaceId;
    }

    @JsonProperty("document_id")
    public Long getDocumentId() {
        return documentId;
    }

    @JsonProperty("document_title")
    public String getDocumentTitle() {
        return documentTitle;
    }

    @JsonProperty("chunk_index")
    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    @JsonProperty("token_count")
    public Integer getTokenCount() {
        return tokenCount;
    }
}