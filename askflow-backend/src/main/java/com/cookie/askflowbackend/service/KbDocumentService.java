package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;

import java.util.List;

public interface KbDocumentService {

    KbDocumentResponse createDocument(Long spaceId, CreateKbDocumentRequest request);

    List<KbDocumentResponse> listDocuments(Long spaceId);

    KbDocumentResponse getDocumentDetail(Long id);
}