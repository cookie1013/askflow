package com.cookie.askflowbackend.controller;

import com.cookie.askflowbackend.client.AiClient;
import com.cookie.askflowbackend.common.ApiResponse;
import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.ChatAskRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiClient aiClient;

    public ChatController(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    @PostMapping("/ask")
    public ApiResponse<AiAskResponse> ask(@Valid @RequestBody ChatAskRequest request) {
        AiAskResponse response = aiClient.ask(request.getQuestion());
        return ApiResponse.success(response);
    }
}