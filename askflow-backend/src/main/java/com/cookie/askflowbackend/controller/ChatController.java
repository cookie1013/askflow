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
import com.cookie.askflowbackend.service.HybridRetrievalService;
import com.cookie.askflowbackend.service.QaSessionService;
import com.cookie.askflowbackend.service.RagTraceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.cookie.askflowbackend.dto.RagRetrievedChunk;
import com.cookie.askflowbackend.service.HybridRetrievalService;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final int DEFAULT_TOP_K = 3;
    private static final double DEFAULT_MIN_SCORE = 0.35;
    private final HybridRetrievalService hybridRetrievalService;
    private final AiClient aiClient;
    private final AiVectorClient aiVectorClient;
    private final QaSessionService qaSessionService;
    private final RagTraceService ragTraceService;

    public ChatController(AiClient aiClient,
                          AiVectorClient aiVectorClient,
                          QaSessionService qaSessionService,
                          RagTraceService ragTraceService,
                          HybridRetrievalService hybridRetrievalService) {
        this.aiClient = aiClient;
        this.aiVectorClient = aiVectorClient;
        this.qaSessionService = qaSessionService;
        this.ragTraceService = ragTraceService;
        this.hybridRetrievalService = hybridRetrievalService;
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

        List<RagRetrievedChunk> retrievedChunks = hybridRetrievalService.retrieve(
                request.getSpaceId(),
                request.getQuestion(),
                DEFAULT_TOP_K,
                DEFAULT_MIN_SCORE
        );

        long retrievalTimeMs = System.currentTimeMillis() - retrievalStart;

        System.out.println("Hybrid RAG search question=" + request.getQuestion()
                + ", hit size=" + retrievedChunks.size()
                + ", retrievalTimeMs=" + retrievalTimeMs);

        List<AiContextChunk> contextChunks = retrievedChunks.stream()
                .map(chunk -> new AiContextChunk(
                        String.valueOf(chunk.getChunkId()),
                        chunk.getDocumentTitle(),
                        chunk.getContent(),
                        chunk.getScore(),
                        chunk.getPageNo(),
                        chunk.getChunkType(),
                        chunk.getSectionTitle()
                ))
                .toList();

        System.out.println("Hybrid RAG context chunk size=" + contextChunks.size());

        long generationStart = System.currentTimeMillis();

        AiAskResponse response = aiClient.ask(request.getQuestion(), contextChunks);

        long generationTimeMs = System.currentTimeMillis() - generationStart;

        response.setSessionId(sessionId);

        qaSessionService.saveChatMessages(sessionId, request.getQuestion(), response);

        long totalTimeMs = System.currentTimeMillis() - totalStart;

        ragTraceService.saveSuccessTraceFromRetrievedChunks(
                request.getSpaceId(),
                sessionId,
                request.getQuestion(),
                null,
                retrievedChunks,
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