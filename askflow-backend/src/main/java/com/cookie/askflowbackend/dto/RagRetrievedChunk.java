package com.cookie.askflowbackend.dto;

public class RagRetrievedChunk {

    private Long chunkId;
    private Long documentId;
    private String documentTitle;
    private Integer chunkIndex;
    private String content;
    private Double score;
    private String source; // vector / keyword / hybrid
    private Integer pageNo;
    private String chunkType;
    private String sectionTitle;
    public RagRetrievedChunk() {
    }
    public RagRetrievedChunk(Long chunkId,
                             Long documentId,
                             String documentTitle,
                             Integer chunkIndex,
                             String content,
                             Double score,
                             String source) {
        this(chunkId, documentId, documentTitle, chunkIndex, content, score, source,
                null, null, null);
    }
    public RagRetrievedChunk(Long chunkId,
                             Long documentId,
                             String documentTitle,
                             Integer chunkIndex,
                             String content,
                             Double score,
                             String source,
                             Integer pageNo,
                             String chunkType,
                             String sectionTitle) {
        this.chunkId = chunkId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.score = score;
        this.source = source;
        this.pageNo = pageNo;
        this.chunkType = chunkType;
        this.sectionTitle = sectionTitle;
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
    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getChunkType() {
        return chunkType;
    }

    public void setChunkType(String chunkType) {
        this.chunkType = chunkType;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
}