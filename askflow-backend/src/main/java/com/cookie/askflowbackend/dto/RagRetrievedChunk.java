package com.cookie.askflowbackend.dto;

public class RagRetrievedChunk {

    private Long chunkId;
    private Long documentId;
    private String documentTitle;
    private Integer chunkIndex;
    private String content;
    private Double score;
    private String source; // vector / keyword / hybrid

    public RagRetrievedChunk() {
    }

    public RagRetrievedChunk(Long chunkId,
                             Long documentId,
                             String documentTitle,
                             Integer chunkIndex,
                             String content,
                             Double score,
                             String source) {
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.score = score;
        this.source = source;
    }

    public Long getChunkId() {
        return chunkId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public Double getScore() {
        return score;
    }

    public String getSource() {
        return source;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setSource(String source) {
        this.source = source;
    }
}