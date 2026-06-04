package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class KbChunkResponse {

    private Long id;
    private Long spaceId;
    private Long documentId;
    private Integer chunkIndex;
    private String content;
    private Integer tokenCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer pageNo;
    private String chunkType;
    private String sectionTitle;
    public KbChunkResponse() {
    }

    public KbChunkResponse(Long id, Long spaceId, Long documentId, Integer chunkIndex,
                           String content, Integer tokenCount, Integer status,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           Integer pageNo,
                           String chunkType,
                           String sectionTitle) {
        this.id = id;
        this.spaceId = spaceId;
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.tokenCount = tokenCount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pageNo = pageNo;
        this.chunkType = chunkType;
        this.sectionTitle = sectionTitle;
        this.pageNo = pageNo;
        this.chunkType = chunkType;
        this.sectionTitle = sectionTitle;
    }

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public Integer getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public String getChunkType() {
        return chunkType;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }
}