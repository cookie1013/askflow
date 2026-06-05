package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorDeleteDocumentResponse {

    @JsonProperty("document_id")
    private Long documentId;

    @JsonProperty("deleted_count")
    private Integer deletedCount;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Integer getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(Integer deletedCount) {
        this.deletedCount = deletedCount;
    }
}