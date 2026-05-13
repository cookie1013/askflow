package com.cookie.askflowbackend.client;

import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.AiContextChunk;
import com.cookie.askflowbackend.dto.AiRagAskRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class AiClient {

    private final RestClient restClient;

    public AiClient(@Value("${askflow.ai-service.base-url}") String aiServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(aiServiceBaseUrl)
                .build();
    }

    public AiAskResponse ask(String question, List<AiContextChunk> contextChunks) {
        AiRagAskRequest request = new AiRagAskRequest(question, contextChunks);

        return restClient.post()
                .uri("/rag/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiAskResponse.class);
    }
}