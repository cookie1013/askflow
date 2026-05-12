package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.KbSearchResponse;
import com.cookie.askflowbackend.service.KbSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/kb/search")
public class KbSearchController {

    private final KbSearchService kbSearchService;

    public KbSearchController(KbSearchService kbSearchService) {
        this.kbSearchService = kbSearchService;
    }

    @GetMapping
    public ApiResponse<List<KbSearchResponse>> search(
            @RequestParam
            @NotNull(message = "spaceId cannot be null")
            Long spaceId,

            @RequestParam
            @NotBlank(message = "keyword cannot be blank")
            String keyword,

            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "limit must be greater than or equal to 1")
            @Max(value = 20, message = "limit must be less than or equal to 20")
            Integer limit) {
        return ApiResponse.success(kbSearchService.search(spaceId, keyword, limit));
    }
}