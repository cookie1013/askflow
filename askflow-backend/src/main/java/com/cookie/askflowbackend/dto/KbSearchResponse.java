package com.cookie.askflowbackend.dto;

public class KbSearchResponse {

    private Long chunkId;
    private Long spaceId;
    private Long documentId;
    private String documentTitle;
    private Integer chunkIndex;
    private String content;
    private Integer tokenCount;

    public KbSearchResponse() {
    }

    public KbSearchResponse(Long chunkId, Long spaceId, Long documentId,
                            String documentTitle, Integer chunkIndex,
                            String content, Integer tokenCount) {
        this.chunkId = chunkId;
        this.spaceId = spaceId;
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.tokenCount = tokenCount;
    }

    public Long getChunkId() {
        return chunkId;
    }

    public Long getSpaceId() {
        return spaceId;
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

    public Integer getTokenCount() {
        return tokenCount;
    }
}