package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.entity.RagTrace;
import com.cookie.askflowbackend.entity.RagTraceChunk;
import com.cookie.askflowbackend.repository.RagTraceChunkRepository;
import com.cookie.askflowbackend.repository.RagTraceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag/traces")
public class RagTraceController {

    private final RagTraceRepository ragTraceRepository;
    private final RagTraceChunkRepository ragTraceChunkRepository;

    public RagTraceController(RagTraceRepository ragTraceRepository,
                              RagTraceChunkRepository ragTraceChunkRepository) {
        this.ragTraceRepository = ragTraceRepository;
        this.ragTraceChunkRepository = ragTraceChunkRepository;
    }

    @GetMapping
    public ApiResponse<List<RagTrace>> listTraces(@RequestParam Long spaceId) {
        return ApiResponse.success(ragTraceRepository.findTop50BySpaceIdOrderByCreatedAtDesc(spaceId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getTraceDetail(@PathVariable Long id) {
        RagTrace trace = ragTraceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("trace not found"));

        List<RagTraceChunk> chunks = ragTraceChunkRepository.findByTraceIdOrderByRankNoAsc(id);

        Map<String, Object> result = new HashMap<>();
        result.put("trace", trace);
        result.put("chunks", chunks);

        return ApiResponse.success(result);
    }
}