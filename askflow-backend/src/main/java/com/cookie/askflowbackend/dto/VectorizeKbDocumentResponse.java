package com.cookie.askflowbackend.dto;

public class VectorizeKbDocumentResponse {

    private Long documentId;
    private String documentTitle;
    private Integer chunkCount;
    private Integer indexedCount;

    public VectorizeKbDocumentResponse() {
    }

    public VectorizeKbDocumentResponse(Long documentId, String documentTitle,
                                       Integer chunkCount, Integer indexedCount) {
        this.documentId = documentId;
        this.documentTitle = documentTitle;
        this.chunkCount = chunkCount;
        this.indexedCount = indexedCount;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public Integer getIndexedCount() {
        return indexedCount;
    }
}