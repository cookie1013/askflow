package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class QaSessionResponse {

    private Long id;
    private Long spaceId;
    private String title;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QaSessionResponse() {
    }

    public QaSessionResponse(Long id, Long spaceId, String title, Integer status,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.spaceId = spaceId;
        this.title = title;
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