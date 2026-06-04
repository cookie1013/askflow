package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.RagTraceChunkResponse;
import com.cookie.askflowbackend.dto.RagTraceDetailResponse;
import com.cookie.askflowbackend.dto.RagTraceListItemResponse;
import com.cookie.askflowbackend.entity.RagTrace;
import com.cookie.askflowbackend.entity.RagTraceChunk;
import com.cookie.askflowbackend.repository.RagTraceChunkRepository;
import com.cookie.askflowbackend.repository.RagTraceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag/traces")
public class RagTraceController {

    private final RagTraceRepository ragTraceRepository;
    private final RagTraceChunkRepository ragTraceChunkRepository;
    private final ObjectMapper objectMapper;

    public RagTraceController(RagTraceRepository ragTraceRepository,
                              RagTraceChunkRepository ragTraceChunkRepository,
                              ObjectMapper objectMapper) {
        this.ragTraceRepository = ragTraceRepository;
        this.ragTraceChunkRepository = ragTraceChunkRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ApiResponse<List<RagTraceListItemResponse>> listTraces(@RequestParam Long spaceId) {
        List<RagTraceListItemResponse> result = ragTraceRepository.findTop50BySpaceIdOrderByCreatedAtDesc(spaceId)
                .stream()
                .map(trace -> new RagTraceListItemResponse(
                        trace.getId(),
                        trace.getSpaceId(),
                        trace.getSessionId(),
                        trace.getQuestion(),
                        trace.getRetrievalMode(),
                        trace.getTopK(),
                        trace.getMinScore(),
                        trace.getContextCount(),
                        trace.getRetrievalTimeMs(),
                        trace.getGenerationTimeMs(),
                        trace.getTotalTimeMs(),
                        trace.getRefused(),
                        trace.getStatus(),
                        trace.getCreatedAt()
                ))
                .toList();

        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<RagTraceDetailResponse> getTraceDetail(@PathVariable Long id) {
        RagTrace trace = ragTraceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("trace not found"));

        List<RagTraceChunk> chunks = ragTraceChunkRepository.findByTraceIdOrderByRankNoAsc(id);

        List<RagTraceChunkResponse> chunkResponses = chunks.stream()
                .map(chunk -> new RagTraceChunkResponse(
                        chunk.getChunkId(),
                        chunk.getDocumentId(),
                        chunk.getDocumentTitle(),
                        chunk.getChunkIndex(),
                        chunk.getRankNo(),
                        chunk.getScore(),
                        chunk.getSource(),
                        chunk.getSelected(),
                        chunk.getContentSnapshot()
                ))
                .toList();

        RagTraceDetailResponse response = new RagTraceDetailResponse(
                trace.getId(),
                trace.getSpaceId(),
                trace.getSessionId(),
                trace.getQuestion(),
                trace.getRewrittenQuery(),
                trace.getAnswer(),
                trace.getRetrievalMode(),
                trace.getTopK(),
                trace.getMinScore(),
                trace.getContextCount(),
                parseJsonList(trace.getRetrievalScoresJson()),
                parseJsonList(trace.getCitationsJson()),
                chunkResponses,
                trace.getRetrievalTimeMs(),
                trace.getGenerationTimeMs(),
                trace.getTotalTimeMs(),
                trace.getRefused(),
                trace.getStatus(),
                trace.getErrorMessage(),
                trace.getCreatedAt()
        );

        return ApiResponse.success(response);
    }

    private List<Map<String, Object>> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }
}