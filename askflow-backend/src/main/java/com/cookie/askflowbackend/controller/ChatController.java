package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.client.AiClient;
import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.AiContextChunk;
import com.cookie.askflowbackend.dto.ChatAskRequest;
import com.cookie.askflowbackend.dto.KbSearchResponse;
import com.cookie.askflowbackend.service.KbSearchService;
import com.cookie.askflowbackend.service.QaSessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiClient aiClient;
    private final KbSearchService kbSearchService;
    private final QaSessionService qaSessionService;

    public ChatController(AiClient aiClient,
                          KbSearchService kbSearchService,
                          QaSessionService qaSessionService) {
        this.aiClient = aiClient;
        this.kbSearchService = kbSearchService;
        this.qaSessionService = qaSessionService;
    }

    @PostMapping("/ask")
    public ApiResponse<AiAskResponse> ask(@Valid @RequestBody ChatAskRequest request) {
        Long sessionId = qaSessionService.resolveSession(
                request.getSessionId(),
                request.getSpaceId(),
                request.getQuestion()
        );

        String keyword = request.getKeyword();

        if (keyword == null || keyword.isBlank()) {
            keyword = request.getQuestion();
        }

        List<KbSearchResponse> searchResults = kbSearchService.search(
                request.getSpaceId(),
                keyword,
                5
        );

        System.out.println("RAG search keyword=" + keyword + ", result size=" + searchResults.size());

        List<AiContextChunk> contextChunks = searchResults.stream()
                .map(item -> new AiContextChunk(
                        String.valueOf(item.getChunkId()),
                        item.getDocumentTitle(),
                        item.getContent()
                ))
                .toList();

        System.out.println("RAG context chunk size=" + contextChunks.size());

        AiAskResponse response = aiClient.ask(request.getQuestion(), contextChunks);
        response.setSessionId(sessionId);

        qaSessionService.saveChatMessages(sessionId, request.getQuestion(), response);

        return ApiResponse.success(response);
    }
}