package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatAskRequest {

    @NotBlank(message = "question cannot be blank")
    private String question;

    public ChatAskRequest() {
    }

    public ChatAskRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}