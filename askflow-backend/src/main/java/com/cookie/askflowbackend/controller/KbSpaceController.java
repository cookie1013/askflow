package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateKbSpaceRequest;
import com.cookie.askflowbackend.dto.KbSpaceResponse;
import com.cookie.askflowbackend.service.KbSpaceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kb/spaces")
public class KbSpaceController {

    private final KbSpaceService kbSpaceService;

    public KbSpaceController(KbSpaceService kbSpaceService) {
        this.kbSpaceService = kbSpaceService;
    }

    @PostMapping
    public ApiResponse<KbSpaceResponse> createSpace(@Valid @RequestBody CreateKbSpaceRequest request) {
        return ApiResponse.success(kbSpaceService.createSpace(request));
    }

    @GetMapping
    public ApiResponse<List<KbSpaceResponse>> listSpaces() {
        return ApiResponse.success(kbSpaceService.listSpaces());
    }

    @GetMapping("/{id}")
    public ApiResponse<KbSpaceResponse> getSpaceDetail(@PathVariable Long id) {
        return ApiResponse.success(kbSpaceService.getSpaceDetail(id));
    }
}