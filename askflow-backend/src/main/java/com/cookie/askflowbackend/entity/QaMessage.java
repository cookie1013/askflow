package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qa_message")
public class QaMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属会话 ID
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    /**
     * 消息角色：USER / ASSISTANT
     */
    @Column(name = "role", nullable = false, length = 30)
    private String role;

    /**
     * 消息正文
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * AI 回答对应的引用来源 JSON
     */
    @Column(name = "citations_json", columnDefinition = "TEXT")
    private String citationsJson;

    /**
     * 检索与生成过程中的调试信息 JSON
     */
    @Column(name = "debug_json", columnDefinition = "TEXT")
    private String debugJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public QaMessage() {
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public String getCitationsJson() {
        return citationsJson;
    }

    public void setCitationsJson(String citationsJson) {
        this.citationsJson = citationsJson;
    }

    public String getDebugJson() {
        return debugJson;
    }

    public void setDebugJson(String debugJson) {
        this.debugJson = debugJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}