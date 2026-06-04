package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateRagEvalCaseRequest;
import com.cookie.askflowbackend.dto.RagEvalSummaryResponse;
import com.cookie.askflowbackend.entity.RagEvalCase;
import com.cookie.askflowbackend.entity.RagEvalResult;
import com.cookie.askflowbackend.service.RagEvalService;
import com.cookie.askflowbackend.dto.BatchCreateRagEvalCaseRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rag/eval")
public class RagEvalController {

    private final RagEvalService ragEvalService;

    public RagEvalController(RagEvalService ragEvalService) {
        this.ragEvalService = ragEvalService;
    }

    @PostMapping("/cases")
    public ApiResponse<RagEvalCase> createCase(@Valid @RequestBody CreateRagEvalCaseRequest request) {
        return ApiResponse.success(ragEvalService.createCase(request));
    }

    @GetMapping("/cases")
    public ApiResponse<List<RagEvalCase>> listCases(@RequestParam Long spaceId) {
        return ApiResponse.success(ragEvalService.listCases(spaceId));
    }

    @PostMapping("/cases/{id}/run")
    public ApiResponse<RagEvalResult> runCase(
            @PathVariable Long id,
            @RequestParam(defaultValue = "hybrid") String retrievalMode) {
        return ApiResponse.success(ragEvalService.runCase(id, retrievalMode));
    }

    @GetMapping("/cases/{id}/results")
    public ApiResponse<List<RagEvalResult>> listCaseResults(@PathVariable Long id) {
        return ApiResponse.success(ragEvalService.listResultsByCase(id));
    }

    @GetMapping("/summary")
    public ApiResponse<RagEvalSummaryResponse> summary(
            @RequestParam Long spaceId,
            @RequestParam(defaultValue = "hybrid") String retrievalMode) {
        return ApiResponse.success(ragEvalService.summary(spaceId, retrievalMode));
    }
    @PostMapping("/run-all")
    public ApiResponse<List<RagEvalResult>> runAll(
            @RequestParam Long spaceId,
            @RequestParam(defaultValue = "hybrid") String retrievalMode) {
        return ApiResponse.success(ragEvalService.runAll(spaceId, retrievalMode));
    }
    @PostMapping("/cases/batch")
    public ApiResponse<List<RagEvalCase>> createCases(@Valid @RequestBody BatchCreateRagEvalCaseRequest request) {
        return ApiResponse.success(ragEvalService.createCases(request));
    }
}