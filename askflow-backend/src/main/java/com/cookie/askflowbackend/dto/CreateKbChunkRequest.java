package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateKbChunkRequest {

    @NotBlank(message = "chunk content cannot be blank")
    @Size(max = 5000, message = "chunk content length must be less than or equal to 5000")
    private String content;

    public String getContent() {
        return content;
    }
}