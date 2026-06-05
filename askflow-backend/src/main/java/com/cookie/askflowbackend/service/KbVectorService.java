package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.VectorizeKbDocumentResponse;
import com.cookie.askflowbackend.dto.AiVectorDeleteDocumentResponse;
public interface KbVectorService {

    VectorizeKbDocumentResponse vectorizeDocument(Long documentId);
    AiVectorDeleteDocumentResponse deleteVectorsByDocumentId(Long documentId);
}