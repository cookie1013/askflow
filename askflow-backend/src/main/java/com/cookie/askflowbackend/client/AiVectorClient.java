package com.cookie.askflowbackend.client;

import com.cookie.askflowbackend.dto.AiVectorUpsertRequest;
import com.cookie.askflowbackend.dto.AiVectorUpsertResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AiVectorClient {

    private final String aiServiceBaseUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public AiVectorClient(@Value("${askflow.ai-service.base-url}") String aiServiceBaseUrl,
                          ObjectMapper objectMapper) {
        this.aiServiceBaseUrl = aiServiceBaseUrl;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public AiVectorUpsertResponse upsert(AiVectorUpsertRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            System.out.println("Vector upsert request body = " + jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<AiVectorUpsertResponse> response = restTemplate.exchange(
                    aiServiceBaseUrl + "/vector/upsert",
                    HttpMethod.POST,
                    entity,
                    AiVectorUpsertResponse.class
            );

            return response.getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize vector upsert request", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}