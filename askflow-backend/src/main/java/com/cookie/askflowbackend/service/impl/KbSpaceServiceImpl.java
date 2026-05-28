package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.CreateKbSpaceRequest;
import com.cookie.askflowbackend.dto.KbSpaceResponse;
import com.cookie.askflowbackend.entity.KbSpace;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.service.KbSpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KbSpaceServiceImpl implements KbSpaceService {

    private final KbSpaceRepository kbSpaceRepository;
    private final KbDocumentRepository kbDocumentRepository;
    private final KbDocumentChunkRepository kbDocumentChunkRepository;

    public KbSpaceServiceImpl(KbSpaceRepository kbSpaceRepository,
                              KbDocumentRepository kbDocumentRepository,
                              KbDocumentChunkRepository kbDocumentChunkRepository) {
        this.kbSpaceRepository = kbSpaceRepository;
        this.kbDocumentRepository = kbDocumentRepository;
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
    }

    @Override
    public KbSpaceResponse createSpace(CreateKbSpaceRequest request) {
        if (kbSpaceRepository.existsByNameAndStatus(request.getName(), 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "space name already exists");
        }

        KbSpace space = new KbSpace();
        space.setName(request.getName());
        space.setDescription(request.getDescription());
        space.setStatus(1);
        space.setDocumentCount(0);

        KbSpace saved = kbSpaceRepository.save(space);
        return toResponse(saved);
    }

    @Override
    public List<KbSpaceResponse> listSpaces() {
        return kbSpaceRepository.findByStatusOrderByCreatedAtDesc(1)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public KbSpaceResponse getSpaceDetail(Long id) {
        KbSpace space = kbSpaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        return toResponse(space);
    }

    @Transactional
    @Override
    public void deleteSpace(Long id) {
        KbSpace space = kbSpaceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (!Integer.valueOf(1).equals(space.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found");
        }

        String deletedName = space.getName() + "__deleted__" + space.getId();

        space.setName(deletedName);
        space.setStatus(0);
        space.setDocumentCount(0);
        kbSpaceRepository.save(space);

        kbDocumentRepository.softDeleteBySpaceId(id);
        kbDocumentChunkRepository.softDeleteBySpaceId(id);
    }

    private KbSpaceResponse toResponse(KbSpace space) {
        return new KbSpaceResponse(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getStatus(),
                space.getDocumentCount(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }
}