package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.QaMessageResponse;
import com.cookie.askflowbackend.dto.QaSessionResponse;
import com.cookie.askflowbackend.service.QaSessionService;
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

    @GetMapping
    public ApiResponse<List<QaSessionResponse>> listSessions(
            @RequestParam(required = false) Long spaceId) {
        if (spaceId == null) {
            return ApiResponse.success(qaSessionService.listSessions());
        }

        return ApiResponse.success(qaSessionService.listSessions(spaceId));
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<List<QaMessageResponse>> listMessages(
            @PathVariable
            @Positive(message = "session id must be positive")
            Long id) {
        return ApiResponse.success(qaSessionService.listMessages(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSession(
            @PathVariable
            @Positive(message = "session id must be positive")
            Long id) {
        qaSessionService.deleteSession(id);
        return ApiResponse.success(null);
    }
}