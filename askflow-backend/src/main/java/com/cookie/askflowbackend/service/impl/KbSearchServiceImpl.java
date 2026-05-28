package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.KbSearchResponse;
import com.cookie.askflowbackend.entity.KbDocument;
import com.cookie.askflowbackend.entity.KbDocumentChunk;
import com.cookie.askflowbackend.entity.KbSpace;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.service.KbSearchService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KbSearchServiceImpl implements KbSearchService {

    private final KbDocumentChunkRepository kbDocumentChunkRepository;
    private final KbDocumentRepository kbDocumentRepository;
    private final KbSpaceRepository kbSpaceRepository;

    public KbSearchServiceImpl(KbDocumentChunkRepository kbDocumentChunkRepository,
                               KbDocumentRepository kbDocumentRepository,
                               KbSpaceRepository kbSpaceRepository) {
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
        this.kbDocumentRepository = kbDocumentRepository;
        this.kbSpaceRepository = kbSpaceRepository;
    }

    @Override
    public List<KbSearchResponse> search(Long spaceId, String keyword, Integer limit) {
        if (spaceId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "spaceId cannot be null");
        }

        KbSpace space = kbSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "keyword cannot be blank");
        }

        int safeLimit = limit == null || limit < 1 ? 5 : Math.min(limit, 20);

        List<KbDocumentChunk> chunks = kbDocumentChunkRepository.searchByKeyword(
                spaceId,
                keyword.trim(),
                PageRequest.of(0, safeLimit)
        );

        List<Long> documentIds = chunks.stream()
                .map(KbDocumentChunk::getDocumentId)
                .distinct()
                .toList();

        Map<Long, String> documentTitleMap = kbDocumentRepository.findAllById(documentIds)
                .stream()
                .collect(Collectors.toMap(KbDocument::getId, KbDocument::getTitle));

        return chunks.stream()
                .map(chunk -> new KbSearchResponse(
                        chunk.getId(),
                        chunk.getSpaceId(),
                        chunk.getDocumentId(),
                        documentTitleMap.getOrDefault(chunk.getDocumentId(), ""),
                        chunk.getChunkIndex(),
                        chunk.getContent(),
                        chunk.getTokenCount()
                ))
                .toList();
    }
}