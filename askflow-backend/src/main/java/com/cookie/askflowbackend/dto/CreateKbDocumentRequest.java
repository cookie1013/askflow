package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateKbDocumentRequest {

    @NotBlank(message = "document title cannot be blank")
    @Size(max = 200, message = "document title length must be less than or equal to 200")
    private String title;

    @NotBlank(message = "documentType cannot be blank")
    @Size(max = 50, message = "documentType length must be less than or equal to 50")
    private String documentType;

    @Size(max = 255, message = "originalFilename length must be less than or equal to 255")
    private String originalFilename;

    @Size(max = 500, message = "storagePath length must be less than or equal to 500")
    private String storagePath;

    public String getTitle() {
        return title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }
}