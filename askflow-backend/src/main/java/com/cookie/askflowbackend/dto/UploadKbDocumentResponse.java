package com.cookie.askflowbackend.dto;

import java.util.List;

public class UploadKbDocumentResponse {

    private KbDocumentResponse document;
    private List<KbChunkResponse> chunks;

    public UploadKbDocumentResponse() {
    }

    public UploadKbDocumentResponse(KbDocumentResponse document, List<KbChunkResponse> chunks) {
        this.document = document;
        this.chunks = chunks;
    }

    public KbDocumentResponse getDocument() {
        return document;
    }

    public List<KbChunkResponse> getChunks() {
        return chunks;
    }
}