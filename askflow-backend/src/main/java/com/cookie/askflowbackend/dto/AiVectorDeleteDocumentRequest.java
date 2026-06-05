package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorDeleteDocumentRequest {

    @JsonProperty("document_id")
    private Long documentId;

    public AiVectorDeleteDocumentRequest() {
    }

    public AiVectorDeleteDocumentRequest(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
}