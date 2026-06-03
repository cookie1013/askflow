package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rag_eval_result")
public class RagEvalResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "question", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "answer", columnDefinition = "LONGTEXT")
    private String answer;

    @Column(name = "hit_chunk_ids_json", columnDefinition = "JSON")
    private String hitChunkIdsJson;

    @Column(name = "citation_chunk_ids_json", columnDefinition = "JSON")
    private String citationChunkIdsJson;

    @Column(name = "retrieval_scores_json", columnDefinition = "JSON")
    private String retrievalScoresJson;

    @Column(name = "recall_hit")
    private Boolean recallHit;

    @Column(name = "citation_hit")
    private Boolean citationHit;

    @Column(name = "refusal_correct")
    private Boolean refusalCorrect;

    @Column(name = "answer_keyword_hit")
    private Boolean answerKeywordHit;

    @Column(name = "passed", nullable = false)
    private Boolean passed = false;

    @Column(name = "retrieval_time_ms")
    private Long retrievalTimeMs;

    @Column(name = "generation_time_ms")
    private Long generationTimeMs;

    @Column(name = "total_time_ms")
    private Long totalTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getHitChunkIdsJson() {
        return hitChunkIdsJson;
    }

    public void setHitChunkIdsJson(String hitChunkIdsJson) {
        this.hitChunkIdsJson = hitChunkIdsJson;
    }

    public String getCitationChunkIdsJson() {
        return citationChunkIdsJson;
    }

    public void setCitationChunkIdsJson(String citationChunkIdsJson) {
        this.citationChunkIdsJson = citationChunkIdsJson;
    }

    public String getRetrievalScoresJson() {
        return retrievalScoresJson;
    }

    public void setRetrievalScoresJson(String retrievalScoresJson) {
        this.retrievalScoresJson = retrievalScoresJson;
    }

    public Boolean getRecallHit() {
        return recallHit;
    }

    public void setRecallHit(Boolean recallHit) {
        this.recallHit = recallHit;
    }

    public Boolean getCitationHit() {
        return citationHit;
    }

    public void setCitationHit(Boolean citationHit) {
        this.citationHit = citationHit;
    }

    public Boolean getRefusalCorrect() {
        return refusalCorrect;
    }

    public void setRefusalCorrect(Boolean refusalCorrect) {
        this.refusalCorrect = refusalCorrect;
    }

    public Boolean getAnswerKeywordHit() {
        return answerKeywordHit;
    }

    public void setAnswerKeywordHit(Boolean answerKeywordHit) {
        this.answerKeywordHit = answerKeywordHit;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Long getRetrievalTimeMs() {
        return retrievalTimeMs;
    }

    public void setRetrievalTimeMs(Long retrievalTimeMs) {
        this.retrievalTimeMs = retrievalTimeMs;
    }

    public Long getGenerationTimeMs() {
        return generationTimeMs;
    }

    public void setGenerationTimeMs(Long generationTimeMs) {
        this.generationTimeMs = generationTimeMs;
    }

    public Long getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(Long totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}