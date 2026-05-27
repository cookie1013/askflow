package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.CreateQaSessionRequest;
import com.cookie.askflowbackend.dto.QaMessageResponse;
import com.cookie.askflowbackend.dto.QaSessionResponse;
import com.cookie.askflowbackend.service.QaSessionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/qa/sessions")
public class QaSessionController {

    private final QaSessionService qaSessionService;

    public QaSessionController(QaSessionService qaSessionService) {
        this.qaSessionService = qaSessionService;
    }

    @PostMapping
    public ApiResponse<QaSessionResponse> createSession(@Valid @RequestBody CreateQaSessionRequest request) {
        return ApiResponse.success(qaSessionService.createSession(request));
    }

    @GetMapping
    public ApiResponse<List<QaSessionResponse>> listSessions() {
        return ApiResponse.success(qaSessionService.listSessions());
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<List<QaMessageResponse>> listMessages(
            @PathVariable
            @Positive(message = "session id must be positive")
            Long id) {
        return ApiResponse.success(qaSessionService.listMessages(id));
    }
}