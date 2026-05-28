package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QaSessionDetailResponse {

    private Long id;
    private Long spaceId;
    private String title;
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QaMessageResponse> messages;

    public QaSessionDetailResponse(Long id, Long spaceId, String title,
                                   Integer messageCount,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt,
                                   List<QaMessageResponse> messages) {
        this.id = id;
        this.spaceId = spaceId;
        this.title = title;
        this.messageCount = messageCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.messages = messages;
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

    public List<QaMessageResponse> getMessages() {
        return messages;
    }
}