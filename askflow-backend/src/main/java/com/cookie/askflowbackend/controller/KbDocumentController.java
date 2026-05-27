package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;
import com.cookie.askflowbackend.service.KbDocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@RestController
public class KbDocumentController {

    private final KbDocumentService kbDocumentService;

    public KbDocumentController(KbDocumentService kbDocumentService) {
        this.kbDocumentService = kbDocumentService;
    }

    @PostMapping("/api/kb/spaces/{spaceId}/documents")
    public ApiResponse<KbDocumentResponse> createDocument(
            @PathVariable
            @Positive(message = "space id must be positive")
            Long spaceId,
            @Valid @RequestBody CreateKbDocumentRequest request) {
        return ApiResponse.success(kbDocumentService.createDocument(spaceId, request));
    }

    @GetMapping("/api/kb/spaces/{spaceId}/documents")
    public ApiResponse<List<KbDocumentResponse>> listDocuments(
            @PathVariable
            @Positive(message = "space id must be positive")
            Long spaceId) {
        return ApiResponse.success(kbDocumentService.listDocuments(spaceId));
    }

    @GetMapping("/api/kb/documents/{id}")
    public ApiResponse<KbDocumentResponse> getDocumentDetail(
            @PathVariable
            @Positive(message = "document id must be positive")
            Long id) {
        return ApiResponse.success(kbDocumentService.getDocumentDetail(id));
    }
}