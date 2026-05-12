package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.CreateKbSpaceRequest;
import com.cookie.askflowbackend.dto.KbSpaceResponse;
import com.cookie.askflowbackend.entity.KbSpace;
import com.cookie.askflowbackend.repository.KbSpaceRepository;
import com.cookie.askflowbackend.service.KbSpaceService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class KbSpaceServiceImpl implements KbSpaceService {

    private final KbSpaceRepository kbSpaceRepository;

    public KbSpaceServiceImpl(KbSpaceRepository kbSpaceRepository) {
        this.kbSpaceRepository = kbSpaceRepository;
    }

    @Override
    public KbSpaceResponse createSpace(CreateKbSpaceRequest request) {
        if (kbSpaceRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "space name already exists");
        }

        KbSpace space = new KbSpace();
        space.setName(request.getName());
        space.setDescription(request.getDescription());

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

        return toResponse(space);
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