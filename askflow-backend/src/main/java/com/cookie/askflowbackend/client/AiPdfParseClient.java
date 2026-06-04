package com.cookie.askflowbackend.client;

import com.cookie.askflowbackend.dto.AiPdfParseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AiPdfParseClient {

    private final String aiServiceBaseUrl;
    private final RestTemplate restTemplate;

    public AiPdfParseClient(@Value("${askflow.ai-service.base-url}") String aiServiceBaseUrl) {
        this.aiServiceBaseUrl = aiServiceBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    public AiPdfParseResponse parsePdf(MultipartFile file) {
        try {
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<AiPdfParseResponse> response = restTemplate.exchange(
                    aiServiceBaseUrl + "/parse/pdf",
                    HttpMethod.POST,
                    requestEntity,
                    AiPdfParseResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("failed to parse pdf by ai service", e);
        }
    }
}