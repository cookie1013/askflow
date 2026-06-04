package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class KbDocumentResponse {

    private Long id;
    private Long spaceId;
    private String title;
    private String documentType;
    private String originalFilename;
    private String storagePath;
    private String parseStatus;
    private Integer chunkCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public KbDocumentResponse() {
    }

    public KbDocumentResponse(Long id, Long spaceId, String title, String documentType,
                              String originalFilename, String storagePath, String parseStatus,
                              Integer chunkCount, Integer status,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.spaceId = spaceId;
        this.title = title;
        this.documentType = documentType;
        this.originalFilename = originalFilename;
        this.storagePath = storagePath;
        this.parseStatus = parseStatus;
        this.chunkCount = chunkCount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public String getTitle() {
        return title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getParseStatus() {
        return parseStatus;
    }

    public Integer getChunkCount() {
        return chunkCount;
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
}