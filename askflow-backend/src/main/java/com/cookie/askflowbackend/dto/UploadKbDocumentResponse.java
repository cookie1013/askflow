package com.cookie.askflowbackend.dto;

import java.util.List;

public class UploadKbDocumentResponse {

    private KbDocumentResponse document;
    private List<KbChunkResponse> chunks;

    /**
     * 本次上传后成功写入向量库的 chunk 数量。
     */
    private Integer indexedCount;

    /**
     * 向量化状态：SUCCESS / FAILED / NOT_TRIGGERED
     */
    private String vectorStatus;

    public UploadKbDocumentResponse() {
    }

    public UploadKbDocumentResponse(KbDocumentResponse document, List<KbChunkResponse> chunks) {
        this(document, chunks, null, "NOT_TRIGGERED");
    }

    public UploadKbDocumentResponse(KbDocumentResponse document,
                                    List<KbChunkResponse> chunks,
                                    Integer indexedCount,
                                    String vectorStatus) {
        this.document = document;
        this.chunks = chunks;
        this.indexedCount = indexedCount;
        this.vectorStatus = vectorStatus;
    }

    public KbDocumentResponse getDocument() {
        return document;
    }

    public List<KbChunkResponse> getChunks() {
        return chunks;
    }

    public Integer getIndexedCount() {
        return indexedCount;
    }

    public String getVectorStatus() {
        return vectorStatus;
    }
}