package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ChatAskRequest {

    /**
     * 会话 ID。
     * 如果传了 sessionId，就把本次问答保存到已有会话；
     * 如果不传 sessionId，后端会自动创建一个新会话。
     */
    @Positive(message = "sessionId must be positive")
    private Long sessionId;

    @NotNull(message = "spaceId cannot be null")
    @Positive(message = "spaceId must be positive")
    private Long spaceId;

    @NotBlank(message = "question cannot be blank")
    @Size(max = 1000, message = "question length must be less than or equal to 1000")
    private String question;

    /**
     * 第一版 RAG 先用 keyword 做检索。
     * 如果不传 keyword，后端会默认用 question 做检索。
     */
    @Size(max = 200, message = "keyword length must be less than or equal to 200")
    private String keyword;

    public ChatAskRequest() {
    }

    public ChatAskRequest(String question) {
        this.question = question;
    }

    public ChatAskRequest(Long spaceId, String question, String keyword) {
        this.spaceId = spaceId;
        this.question = question;
        this.keyword = keyword;
    }

    public ChatAskRequest(Long sessionId, Long spaceId, String question, String keyword) {
        this.sessionId = sessionId;
        this.spaceId = spaceId;
        this.question = question;
        this.keyword = keyword;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}