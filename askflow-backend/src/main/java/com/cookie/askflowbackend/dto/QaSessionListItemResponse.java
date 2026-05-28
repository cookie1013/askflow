package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class QaSessionListItemResponse {

    private Long id;
    private Long spaceId;
    private String title;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QaSessionListItemResponse(Long id, Long spaceId, String title,
                                     Integer messageCount,
                                     LocalDateTime createdAt,
                                     LocalDateTime updatedAt) {
        this.id = id;
        this.spaceId = spaceId;
        this.title = title;
        this.messageCount = messageCount;
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

    public Integer getMessageCount() {
        return messageCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}