package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.client.AiClient;
import com.cookie.askflowbackend.client.AiVectorClient;
import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.AiContextChunk;
import com.cookie.askflowbackend.dto.AiVectorSearchHit;
import com.cookie.askflowbackend.dto.AiVectorSearchRequest;
import com.cookie.askflowbackend.dto.AiVectorSearchResponse;
import com.cookie.askflowbackend.dto.ChatAskRequest;
import com.cookie.askflowbackend.service.QaSessionService;
import com.cookie.askflowbackend.service.RagTraceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final int DEFAULT_TOP_K = 3;
    private static final double DEFAULT_MIN_SCORE = 0.35;

    private final AiClient aiClient;
    private final AiVectorClient aiVectorClient;
    private final QaSessionService qaSessionService;
    private final RagTraceService ragTraceService;

    public ChatController(AiClient aiClient,
                          AiVectorClient aiVectorClient,
                          QaSessionService qaSessionService,
                          RagTraceService ragTraceService) {
        this.aiClient = aiClient;
        this.aiVectorClient = aiVectorClient;
        this.qaSessionService = qaSessionService;
        this.ragTraceService = ragTraceService;
    }

    @PostMapping("/ask")
    public ApiResponse<AiAskResponse> ask(@Valid @RequestBody ChatAskRequest request) {
        long totalStart = System.currentTimeMillis();

        Long sessionId = qaSessionService.resolveSession(
                request.getSessionId(),
                request.getSpaceId(),
                request.getQuestion()
        );

        AiVectorSearchRequest vectorSearchRequest = new AiVectorSearchRequest(
                request.getSpaceId(),
                request.getQuestion(),
                DEFAULT_TOP_K,
                DEFAULT_MIN_SCORE
        );

        long retrievalStart = System.currentTimeMillis();

        AiVectorSearchResponse vectorSearchResponse = aiVectorClient.search(vectorSearchRequest);

        long retrievalTimeMs = System.currentTimeMillis() - retrievalStart;

        List<AiVectorSearchHit> hits = vectorSearchResponse == null || vectorSearchResponse.getHits() == null
                ? List.of()
                : vectorSearchResponse.getHits();

        System.out.println("Vector RAG search question=" + request.getQuestion()
                + ", hit size=" + hits.size()
                + ", retrievalTimeMs=" + retrievalTimeMs);

        List<AiContextChunk> contextChunks = hits.stream()
                .map(hit -> new AiContextChunk(
                        String.valueOf(hit.getChunkId()),
                        hit.getDocumentTitle(),
                        hit.getContent(),
                        hit.getScore()
                ))
                .toList();

        System.out.println("Vector RAG context chunk size=" + contextChunks.size());

        long generationStart = System.currentTimeMillis();

        AiAskResponse response = aiClient.ask(request.getQuestion(), contextChunks);

        long generationTimeMs = System.currentTimeMillis() - generationStart;

        response.setSessionId(sessionId);

        qaSessionService.saveChatMessages(sessionId, request.getQuestion(), response);

        long totalTimeMs = System.currentTimeMillis() - totalStart;

        ragTraceService.saveSuccessTrace(
                request.getSpaceId(),
                sessionId,
                request.getQuestion(),
                null,
                hits,
                response,
                retrievalTimeMs,
                generationTimeMs,
                totalTimeMs,
                DEFAULT_TOP_K,
                DEFAULT_MIN_SCORE
        );

        System.out.println("RAG trace saved, question=" + request.getQuestion()
                + ", totalTimeMs=" + totalTimeMs
                + ", generationTimeMs=" + generationTimeMs);

        return ApiResponse.success(response);
    }
}