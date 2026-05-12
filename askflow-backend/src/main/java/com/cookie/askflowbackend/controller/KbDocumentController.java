package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;
import com.cookie.askflowbackend.service.KbDocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KbDocumentController {

    private final KbDocumentService kbDocumentService;

    public KbDocumentController(KbDocumentService kbDocumentService) {
        this.kbDocumentService = kbDocumentService;
    }

    @PostMapping("/api/kb/spaces/{spaceId}/documents")
    public ApiResponse<KbDocumentResponse> createDocument(
            @PathVariable Long spaceId,
            @Valid @RequestBody CreateKbDocumentRequest request) {
        return ApiResponse.success(kbDocumentService.createDocument(spaceId, request));
    }

    @GetMapping("/api/kb/spaces/{spaceId}/documents")
    public ApiResponse<List<KbDocumentResponse>> listDocuments(@PathVariable Long spaceId) {
        return ApiResponse.success(kbDocumentService.listDocuments(spaceId));
    }

    @GetMapping("/api/kb/documents/{id}")
    public ApiResponse<KbDocumentResponse> getDocumentDetail(@PathVariable Long id) {
        return ApiResponse.success(kbDocumentService.getDocumentDetail(id));
    }
}