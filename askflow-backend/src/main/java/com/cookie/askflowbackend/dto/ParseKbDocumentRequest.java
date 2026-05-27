package com.cookie.askflowbackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ParseKbDocumentRequest {

    @NotBlank(message = "document content cannot be blank")
    @Size(max = 100000, message = "document content length must be less than or equal to 100000")
    private String content;

    @Min(value = 100, message = "chunkSize must be greater than or equal to 100")
    @Max(value = 2000, message = "chunkSize must be less than or equal to 2000")
    private Integer chunkSize = 500;

    public ParseKbDocumentRequest() {
    }

    public ParseKbDocumentRequest(String content, Integer chunkSize) {
        this.content = content;
        this.chunkSize = chunkSize;
    }

    public String getContent() {
        return content;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }
}