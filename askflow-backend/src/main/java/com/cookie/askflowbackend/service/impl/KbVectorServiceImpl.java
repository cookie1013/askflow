package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.client.AiVectorClient;
import com.cookie.askflowbackend.dto.AiVectorChunk;
import com.cookie.askflowbackend.dto.AiVectorUpsertRequest;
import com.cookie.askflowbackend.dto.AiVectorUpsertResponse;
import com.cookie.askflowbackend.dto.VectorizeKbDocumentResponse;
import com.cookie.askflowbackend.entity.KbDocument;
import com.cookie.askflowbackend.entity.KbDocumentChunk;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import com.cookie.askflowbackend.service.KbVectorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KbVectorServiceImpl implements KbVectorService {

    private final KbDocumentRepository kbDocumentRepository;
    private final KbDocumentChunkRepository kbDocumentChunkRepository;
    private final AiVectorClient aiVectorClient;

    public KbVectorServiceImpl(KbDocumentRepository kbDocumentRepository,
                               KbDocumentChunkRepository kbDocumentChunkRepository,
                               AiVectorClient aiVectorClient) {
        this.kbDocumentRepository = kbDocumentRepository;
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
        this.aiVectorClient = aiVectorClient;
    }

    @Override
    public VectorizeKbDocumentResponse vectorizeDocument(Long documentId) {
        KbDocument document = kbDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        if (document.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        }

        List<KbDocumentChunk> chunks = kbDocumentChunkRepository
                .findByDocumentIdAndStatusOrderByChunkIndexAsc(documentId, 1);

        if (chunks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document has no available chunks");
        }

        List<AiVectorChunk> vectorChunks = chunks.stream()
                .map(chunk -> new AiVectorChunk(
                        chunk.getId(),
                        chunk.getSpaceId(),
                        chunk.getDocumentId(),
                        document.getTitle(),
                        chunk.getChunkIndex(),
                        chunk.getContent(),
                        chunk.getTokenCount()
                ))
                .toList();

        AiVectorUpsertResponse response = aiVectorClient.upsert(
                new AiVectorUpsertRequest(vectorChunks)
        );

        Integer indexedCount = response == null || response.getIndexedCount() == null
                ? 0
                : response.getIndexedCount();

        return new VectorizeKbDocumentResponse(
                document.getId(),
                document.getTitle(),
                chunks.size(),
                indexedCount
        );
    }
}