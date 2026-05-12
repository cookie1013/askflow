package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbChunkRequest;
import com.cookie.askflowbackend.dto.KbChunkResponse;
import com.cookie.askflowbackend.service.KbChunkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KbChunkController {

    private final KbChunkService kbChunkService;

    public KbChunkController(KbChunkService kbChunkService) {
        this.kbChunkService = kbChunkService;
    }

    @PostMapping("/api/kb/documents/{documentId}/chunks")
    public ApiResponse<KbChunkResponse> createChunk(
            @PathVariable Long documentId,
            @Valid @RequestBody CreateKbChunkRequest request) {
        return ApiResponse.success(kbChunkService.createChunk(documentId, request));
    }

    @GetMapping("/api/kb/documents/{documentId}/chunks")
    public ApiResponse<List<KbChunkResponse>> listChunks(@PathVariable Long documentId) {
        return ApiResponse.success(kbChunkService.listChunks(documentId));
    }

    @GetMapping("/api/kb/chunks/{id}")
    public ApiResponse<KbChunkResponse> getChunkDetail(@PathVariable Long id) {
        return ApiResponse.success(kbChunkService.getChunkDetail(id));
    }
}