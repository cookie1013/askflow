package com.cookie.askflowbackend.dto;

import java.util.List;

public class AiVectorUpsertRequest {

    private List<AiVectorChunk> chunks;

    public AiVectorUpsertRequest() {
    }

    public AiVectorUpsertRequest(List<AiVectorChunk> chunks) {
        this.chunks = chunks;
    }

    public List<AiVectorChunk> getChunks() {
        return chunks;
    }
}