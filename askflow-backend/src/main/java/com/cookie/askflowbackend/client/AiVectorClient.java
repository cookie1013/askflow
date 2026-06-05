package com.cookie.askflowbackend.client;

import com.cookie.askflowbackend.dto.AiVectorUpsertRequest;
import com.cookie.askflowbackend.dto.AiVectorUpsertResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.cookie.askflowbackend.dto.AiVectorSearchRequest;
import com.cookie.askflowbackend.dto.AiVectorSearchResponse;
import com.cookie.askflowbackend.dto.AiVectorDeleteDocumentRequest;
import com.cookie.askflowbackend.dto.AiVectorDeleteDocumentResponse;
import org.springframework.http.*;
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

    public AiVectorSearchResponse search(AiVectorSearchRequest request) {
        try {
            String jsonBody = objectMapper.writeValueAsString(request);
            System.out.println("Vector search request body = " + jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<AiVectorSearchResponse> response = restTemplate.exchange(
                    aiServiceBaseUrl + "/vector/search",
                    HttpMethod.POST,
                    entity,
                    AiVectorSearchResponse.class
            );

            return response.getBody();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize vector search request", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public AiVectorDeleteDocumentResponse deleteByDocumentId(Long documentId) {
        try {
            AiVectorDeleteDocumentRequest request = new AiVectorDeleteDocumentRequest(documentId);
            String jsonBody = objectMapper.writeValueAsString(request);

            System.out.println("Vector delete request body = " + jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<AiVectorDeleteDocumentResponse> response = restTemplate.exchange(
                    aiServiceBaseUrl + "/vector/delete-by-document",
                    HttpMethod.POST,
                    entity,
                    AiVectorDeleteDocumentResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("failed to delete vectors by documentId: " + documentId, e);
        }
    }
}