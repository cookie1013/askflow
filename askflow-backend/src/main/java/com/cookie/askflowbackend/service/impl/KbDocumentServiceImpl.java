package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.CreateKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbDocumentResponse;
import com.cookie.askflowbackend.entity.KbDocument;
import com.cookie.askflowbackend.entity.KbSpace;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.service.KbDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KbDocumentServiceImpl implements KbDocumentService {

    private final KbDocumentRepository kbDocumentRepository;
    private final KbSpaceRepository kbSpaceRepository;

    public KbDocumentServiceImpl(KbDocumentRepository kbDocumentRepository,
                                 KbSpaceRepository kbSpaceRepository) {
        this.kbDocumentRepository = kbDocumentRepository;
        this.kbSpaceRepository = kbSpaceRepository;
    }

    @Transactional
    @Override
    public KbDocumentResponse createDocument(Long spaceId, CreateKbDocumentRequest request) {
        KbSpace space = kbSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (space.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "space is disabled");
        }

        if (kbDocumentRepository.existsBySpaceIdAndTitle(spaceId, request.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document title already exists in this space");
        }

        KbDocument document = new KbDocument();
        document.setSpaceId(spaceId);
        document.setTitle(request.getTitle());
        document.setDocumentType(request.getDocumentType());
        document.setOriginalFilename(request.getOriginalFilename());
        document.setStoragePath(request.getStoragePath());
        document.setParseStatus("PENDING");
        document.setChunkCount(0);
        document.setStatus(1);

        KbDocument saved = kbDocumentRepository.save(document);

        Integer currentCount = space.getDocumentCount() == null ? 0 : space.getDocumentCount();
        space.setDocumentCount(currentCount + 1);
        kbSpaceRepository.save(space);

        return toResponse(saved);
    }

    @Override
    public List<KbDocumentResponse> listDocuments(Long spaceId) {
        if (!kbSpaceRepository.existsById(spaceId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        return kbDocumentRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public KbDocumentResponse getDocumentDetail(Long id) {
        KbDocument document = kbDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        return toResponse(document);
    }

    private KbDocumentResponse toResponse(KbDocument document) {
        return new KbDocumentResponse(
                document.getId(),
                document.getSpaceId(),
                document.getTitle(),
                document.getDocumentType(),
                document.getOriginalFilename(),
                document.getStoragePath(),
                document.getParseStatus(),
                document.getChunkCount(),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}