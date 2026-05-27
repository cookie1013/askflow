package com.cookie.askflowbackend.service.impl;

import com.cookie.askflowbackend.dto.CreateKbChunkRequest;
import com.cookie.askflowbackend.dto.KbChunkResponse;
import com.cookie.askflowbackend.entity.KbDocument;
import com.cookie.askflowbackend.entity.KbDocumentChunk;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import com.cookie.askflowbackend.service.KbChunkService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import com.cookie.askflowbackend.dto.ParseKbDocumentRequest;
import java.util.ArrayList;
@Service
public class KbChunkServiceImpl implements KbChunkService {

    private final KbDocumentChunkRepository kbDocumentChunkRepository;
    private final KbDocumentRepository kbDocumentRepository;

    public KbChunkServiceImpl(KbDocumentChunkRepository kbDocumentChunkRepository,
                              KbDocumentRepository kbDocumentRepository) {
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
        this.kbDocumentRepository = kbDocumentRepository;
    }

    @Transactional
    @Override
    public KbChunkResponse createChunk(Long documentId, CreateKbChunkRequest request) {
        KbDocument document = kbDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        if (document.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document is disabled");
        }

        long currentChunkCount = kbDocumentChunkRepository.countByDocumentIdAndStatus(documentId, 1);
        int nextChunkIndex = (int) currentChunkCount + 1;

        KbDocumentChunk chunk = new KbDocumentChunk();
        chunk.setSpaceId(document.getSpaceId());
        chunk.setDocumentId(documentId);
        chunk.setChunkIndex(nextChunkIndex);
        chunk.setContent(request.getContent());
        chunk.setTokenCount(estimateTokenCount(request.getContent()));
        chunk.setStatus(1);

        KbDocumentChunk saved = kbDocumentChunkRepository.save(chunk);

        document.setChunkCount(nextChunkIndex);
        document.setParseStatus("PARSED");
        kbDocumentRepository.save(document);

        return toResponse(saved);
    }

    @Override
    public List<KbChunkResponse> listChunks(Long documentId) {
        if (!kbDocumentRepository.existsById(documentId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        }

        return kbDocumentChunkRepository.findByDocumentIdAndStatusOrderByChunkIndexAsc(documentId, 1)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public KbChunkResponse getChunkDetail(Long id) {
        KbDocumentChunk chunk = kbDocumentChunkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chunk not found"));

        return toResponse(chunk);
    }

    private KbChunkResponse toResponse(KbDocumentChunk chunk) {
        return new KbChunkResponse(
                chunk.getId(),
                chunk.getSpaceId(),
                chunk.getDocumentId(),
                chunk.getChunkIndex(),
                chunk.getContent(),
                chunk.getTokenCount(),
                chunk.getStatus(),
                chunk.getCreatedAt(),
                chunk.getUpdatedAt()
        );
    }

    private Integer estimateTokenCount(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        // 小白阶段先用字符数 / 2 做近似估算，后面可以换成真正 tokenizer
        return Math.max(1, content.length() / 2);
    }
    @Transactional
    @Override
    public List<KbChunkResponse> parseDocument(Long documentId, ParseKbDocumentRequest request) {
        KbDocument document = kbDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        if (document.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document is disabled");
        }

        String content = request.getContent();
        int chunkSize = request.getChunkSize() == null ? 500 : request.getChunkSize();

        List<String> pieces = splitText(content, chunkSize);

        kbDocumentChunkRepository.deleteByDocumentId(documentId);

        List<KbDocumentChunk> savedChunks = new ArrayList<>();

        int index = 1;
        for (String piece : pieces) {
            if (piece == null || piece.isBlank()) {
                continue;
            }

            KbDocumentChunk chunk = new KbDocumentChunk();
            chunk.setSpaceId(document.getSpaceId());
            chunk.setDocumentId(documentId);
            chunk.setChunkIndex(index);
            chunk.setContent(piece.trim());
            chunk.setTokenCount(estimateTokenCount(piece));
            chunk.setStatus(1);

            savedChunks.add(kbDocumentChunkRepository.save(chunk));
            index++;
        }

        document.setChunkCount(savedChunks.size());
        document.setParseStatus("PARSED");
        kbDocumentRepository.save(document);

        return savedChunks.stream()
                .map(this::toResponse)
                .toList();
    }
    private List<String> splitText(String content, int chunkSize) {
        String normalized = content
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();

        List<String> chunks = new ArrayList<>();
        String[] paragraphs = normalized.split("\\n\\s*\\n|\\n");

        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {
            String text = paragraph.trim();

            if (text.isBlank()) {
                continue;
            }

            if (text.length() > chunkSize) {
                if (!current.isEmpty()) {
                    chunks.add(current.toString().trim());
                    current.setLength(0);
                }

                for (int start = 0; start < text.length(); start += chunkSize) {
                    int end = Math.min(start + chunkSize, text.length());
                    chunks.add(text.substring(start, end).trim());
                }

                continue;
            }

            if (current.length() + text.length() + 1 > chunkSize) {
                chunks.add(current.toString().trim());
                current.setLength(0);
            }

            if (!current.isEmpty()) {
                current.append("\n");
            }

            current.append(text);
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }
}