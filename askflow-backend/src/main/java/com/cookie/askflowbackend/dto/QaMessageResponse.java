package com.cookie.askflowbackend.dto;

import java.time.LocalDateTime;

public class QaMessageResponse {

    private Long id;
    private Long sessionId;
    private String role;
    private String content;
    private String citationsJson;
    private String debugJson;
    private LocalDateTime createdAt;

    public QaMessageResponse() {
    }

    public QaMessageResponse(Long id, Long sessionId, String role, String content,
                             String citationsJson, String debugJson, LocalDateTime createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.citationsJson = citationsJson;
        this.debugJson = debugJson;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public String getCitationsJson() {
        return citationsJson;
    }

    public String getDebugJson() {
        return debugJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}