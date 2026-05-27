package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;
import com.cookie.askflowbackend.service.KbDocumentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import com.cookie.askflowbackend.dto.UploadKbDocumentResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
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
    @PostMapping(
            value = "/api/kb/spaces/{spaceId}/documents/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<UploadKbDocumentResponse> uploadDocument(
            @PathVariable
            @Positive(message = "space id must be positive")
            Long spaceId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "500")
            @Min(value = 100, message = "chunkSize must be greater than or equal to 100")
            @Max(value = 2000, message = "chunkSize must be less than or equal to 2000")
            Integer chunkSize) {
        return ApiResponse.success(kbDocumentService.uploadAndParseDocument(spaceId, file, chunkSize));
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