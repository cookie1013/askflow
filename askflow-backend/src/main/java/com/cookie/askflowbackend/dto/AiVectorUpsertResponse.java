package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiVectorUpsertResponse {

    @JsonProperty("indexed_count")
    private Integer indexedCount;

    public AiVectorUpsertResponse() {
    }

    public Integer getIndexedCount() {
        return indexedCount;
    }

    public void setIndexedCount(Integer indexedCount) {
        this.indexedCount = indexedCount;
    }
}