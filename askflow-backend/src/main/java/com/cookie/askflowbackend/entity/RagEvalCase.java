package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rag_eval_case")
public class RagEvalCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "expected_chunk_ids_json", columnDefinition = "JSON")
    private String expectedChunkIdsJson;

    @Column(name = "expected_answer_keywords_json", columnDefinition = "JSON")
    private String expectedAnswerKeywordsJson;

    @Column(name = "should_refuse", nullable = false)
    private Boolean shouldRefuse = false;

    @Column(name = "case_type", nullable = false, length = 50)
    private String caseType = "normal";

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
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

    public String getExpectedChunkIdsJson() {
        return expectedChunkIdsJson;
    }

    public void setExpectedChunkIdsJson(String expectedChunkIdsJson) {
        this.expectedChunkIdsJson = expectedChunkIdsJson;
    }

    public String getExpectedAnswerKeywordsJson() {
        return expectedAnswerKeywordsJson;
    }

    public void setExpectedAnswerKeywordsJson(String expectedAnswerKeywordsJson) {
        this.expectedAnswerKeywordsJson = expectedAnswerKeywordsJson;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}