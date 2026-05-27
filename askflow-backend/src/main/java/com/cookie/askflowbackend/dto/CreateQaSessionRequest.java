package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateQaSessionRequest {

    @NotNull(message = "spaceId cannot be null")
    private Long spaceId;

    @NotBlank(message = "session title cannot be blank")
    @Size(max = 200, message = "session title length must be less than or equal to 200")
    private String title;

    public Long getSpaceId() {
        return spaceId;
    }

    public String getTitle() {
        return title;
    }
}