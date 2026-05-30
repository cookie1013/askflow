package com.cookie.askflowbackend.dto;

import java.util.List;
import java.util.Map;

public class AiVectorSearchResponse {

    private List<AiVectorSearchHit> hits;

    private Map<String, Object> debug;

    public List<AiVectorSearchHit> getHits() {
        return hits;
    }

    public Map<String, Object> getDebug() {
        return debug;
    }
}