package com.cookie.askflowbackend.client;

import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.AiContextChunk;
import com.cookie.askflowbackend.dto.AiRagAskRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AiClient {

    private final String aiServiceBaseUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AiClient(@Value("${askflow.ai-service.base-url}") String aiServiceBaseUrl,
                    ObjectMapper objectMapper) {
        this.aiServiceBaseUrl = aiServiceBaseUrl;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public AiAskResponse ask(String question, List<AiContextChunk> contextChunks) {
        AiRagAskRequest request = new AiRagAskRequest(question, contextChunks);

        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            System.out.println("RAG ask request body = " + jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<AiAskResponse> response = restTemplate.exchange(
                    aiServiceBaseUrl + "/rag/ask",
                    HttpMethod.POST,
                    entity,
                    AiAskResponse.class
            );

            return response.getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize rag ask request", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}