package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class KbSpaceResponse {

    private Long id;
    private String name;
    private String description;
    private Integer status;
    private Integer documentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KbSpaceResponse() {
    }

    public KbSpaceResponse(Long id, String name, String description, Integer status,
                           Integer documentCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.documentCount = documentCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}