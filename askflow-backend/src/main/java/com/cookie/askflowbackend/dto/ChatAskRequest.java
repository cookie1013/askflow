package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatAskRequest {

    @NotNull(message = "spaceId cannot be null")
    private Long spaceId;

    @NotBlank(message = "question cannot be blank")
    private String question;

    /**
     * 第一版 RAG 先用 keyword 做检索。
     * 如果不传 keyword，后端会默认用 question 做检索。
     */
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