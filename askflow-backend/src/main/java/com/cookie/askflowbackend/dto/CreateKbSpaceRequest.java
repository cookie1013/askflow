package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateKbSpaceRequest {

    @NotBlank(message = "space name cannot be blank")
    @Size(max = 100, message = "space name length must be less than or equal to 100")
    private String name;

    @Size(max = 500, message = "description length must be less than or equal to 500")
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}