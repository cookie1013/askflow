package com.cookie.askflowbackend.dto;

public class RagTraceChunkResponse {

    private Long chunkId;
    private Long documentId;
    private String documentTitle;
    private Integer chunkIndex;
    private Integer rankNo;
    private Double score;
    private String source;
    private Boolean selected;
    private String contentSnapshot;

    public RagTraceChunkResponse(Long chunkId,
                                 Long documentId,
                                 String documentTitle,
                                 Integer chunkIndex,
                                 Integer rankNo,
                                 Double score,
                                 String source,
                                 Boolean selected,
                                 String contentSnapshot) {
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkIndex = chunkIndex;
        this.rankNo = rankNo;
        this.score = score;
        this.source = source;
        this.selected = selected;
        this.contentSnapshot = contentSnapshot;
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

    public Integer getRankNo() {
        return rankNo;
    }

    public Double getScore() {
        return score;
    }

    public String getSource() {
        return source;
    }

    public Boolean getSelected() {
        return selected;
    }

    public String getContentSnapshot() {
        return contentSnapshot;
    }
}