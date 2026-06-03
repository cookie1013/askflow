package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateRagEvalCaseRequest {

    @NotNull(message = "spaceId cannot be null")
    private Long spaceId;

    @NotBlank(message = "question cannot be blank")
    private String question;

    private List<String> expectedChunkIds;

    private List<String> expectedAnswerKeywords;

    private Boolean shouldRefuse = false;

    private String caseType = "normal";

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

    public List<String> getExpectedChunkIds() {
        return expectedChunkIds;
    }

    public void setExpectedChunkIds(List<String> expectedChunkIds) {
        this.expectedChunkIds = expectedChunkIds;
    }

    public List<String> getExpectedAnswerKeywords() {
        return expectedAnswerKeywords;
    }

    public void setExpectedAnswerKeywords(List<String> expectedAnswerKeywords) {
        this.expectedAnswerKeywords = expectedAnswerKeywords;
    }

    public Boolean getShouldRefuse() {
        return shouldRefuse;
    }

    public void setShouldRefuse(Boolean shouldRefuse) {
        this.shouldRefuse = shouldRefuse;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
}