package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbChunkRequest;
import com.cookie.askflowbackend.dto.KbChunkResponse;
import com.cookie.askflowbackend.service.KbChunkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import com.cookie.askflowbackend.dto.ParseKbDocumentRequest;
import java.util.List;

@Validated
@RestController
public class KbChunkController {

    private final KbChunkService kbChunkService;

    public KbChunkController(KbChunkService kbChunkService) {
        this.kbChunkService = kbChunkService;
    }

    @PostMapping("/api/kb/documents/{documentId}/chunks")
    public ApiResponse<KbChunkResponse> createChunk(
            @PathVariable
            @Positive(message = "document id must be positive")
            Long documentId,
            @Valid @RequestBody CreateKbChunkRequest request) {
        return ApiResponse.success(kbChunkService.createChunk(documentId, request));
    }
    @PostMapping("/api/kb/documents/{documentId}/parse")
    public ApiResponse<List<KbChunkResponse>> parseDocument(
            @PathVariable
            @Positive(message = "document id must be positive")
            Long documentId,
            @Valid @RequestBody ParseKbDocumentRequest request) {
        return ApiResponse.success(kbChunkService.parseDocument(documentId, request));
    }

    @GetMapping("/api/kb/documents/{documentId}/chunks")
    public ApiResponse<List<KbChunkResponse>> listChunks(
            @PathVariable
            @Positive(message = "document id must be positive")
            Long documentId) {
        return ApiResponse.success(kbChunkService.listChunks(documentId));
    }

    @GetMapping("/api/kb/chunks/{id}")
    public ApiResponse<KbChunkResponse> getChunkDetail(
            @PathVariable
            @Positive(message = "chunk id must be positive")
            Long id) {
        return ApiResponse.success(kbChunkService.getChunkDetail(id));
    }
}