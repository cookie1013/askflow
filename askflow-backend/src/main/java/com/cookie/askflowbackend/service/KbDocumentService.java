package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;
import com.cookie.askflowbackend.dto.UploadKbDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KbDocumentService {

    KbDocumentResponse createDocument(Long spaceId, CreateKbDocumentRequest request);

    List<KbDocumentResponse> listDocuments(Long spaceId);

    KbDocumentResponse getDocumentDetail(Long id);

    UploadKbDocumentResponse uploadAndParseDocument(Long spaceId, MultipartFile file, Integer chunkSize);
}