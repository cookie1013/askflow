package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.VectorizeKbDocumentResponse;

public interface KbVectorService {

    VectorizeKbDocumentResponse vectorizeDocument(Long documentId);
}