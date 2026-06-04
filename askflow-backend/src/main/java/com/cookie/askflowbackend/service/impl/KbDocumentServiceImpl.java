package com.cookie.askflowbackend.service.impl;
import com.cookie.askflowbackend.client.AiPdfParseClient;
import com.cookie.askflowbackend.dto.AiPdfParseChunk;
import com.cookie.askflowbackend.dto.AiPdfParseResponse;
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
import com.cookie.askflowbackend.dto.ParseKbDocumentRequest;
import com.cookie.askflowbackend.dto.KbChunkResponse;
import com.cookie.askflowbackend.dto.UploadKbDocumentResponse;
import com.cookie.askflowbackend.service.KbChunkService;
import org.springframework.web.multipart.MultipartFile;
import com.cookie.askflowbackend.entity.KbDocumentChunk;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.List;
import com.cookie.askflowbackend.dto.VectorizeKbDocumentResponse;
import com.cookie.askflowbackend.service.KbVectorService;
import java.util.ArrayList;
@Service
public class KbDocumentServiceImpl implements KbDocumentService {

    private final KbDocumentRepository kbDocumentRepository;
    private final KbSpaceRepository kbSpaceRepository;
    private final KbChunkService kbChunkService;
    private final KbDocumentChunkRepository kbDocumentChunkRepository;
    private final KbVectorService kbVectorService;
    private final AiPdfParseClient aiPdfParseClient;
    public KbDocumentServiceImpl(KbDocumentRepository kbDocumentRepository,
                                 KbSpaceRepository kbSpaceRepository,
                                 KbChunkService kbChunkService,
                                 KbDocumentChunkRepository kbDocumentChunkRepository,
                                 KbVectorService kbVectorService,
                                 AiPdfParseClient aiPdfParseClient) {
        this.kbDocumentRepository = kbDocumentRepository;
        this.kbSpaceRepository = kbSpaceRepository;
        this.kbChunkService = kbChunkService;
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
        this.kbVectorService = kbVectorService;
        this.aiPdfParseClient = aiPdfParseClient;
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

        if (document.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        }

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
    private boolean isPdfFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        return (filename != null && filename.toLowerCase().endsWith(".pdf"))
                || "application/pdf".equalsIgnoreCase(contentType);
    }
    private String removeBom(String content) {
        if (content == null) {
            return null;
        }

        if (content.startsWith("\uFEFF")) {
            return content.substring(1);
        }

        return content;
    }
    @Transactional
    @Override
    public UploadKbDocumentResponse uploadAndParseDocument(Long spaceId, MultipartFile file, Integer chunkSize) {
        KbSpace space = kbSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        if (space.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "space is disabled");
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uploaded file cannot be empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uploaded file size must be less than or equal to 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "original filename cannot be blank");
        }

        String safeFilename = Paths.get(originalFilename).getFileName().toString();
        String lowerFilename = safeFilename.toLowerCase(Locale.ROOT);

        boolean isTxt = lowerFilename.endsWith(".txt");
        boolean isMarkdown = lowerFilename.endsWith(".md");
        boolean isPdf = lowerFilename.endsWith(".pdf")
                || "application/pdf".equalsIgnoreCase(file.getContentType());

        if (!isTxt && !isMarkdown && !isPdf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only .txt, .md and .pdf files are supported");
        }

        String title = buildTitleFromFilename(safeFilename);

        if (kbDocumentRepository.existsBySpaceIdAndTitleAndStatus(spaceId, title, 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document title already exists in this space");
        }

        KbDocument document = new KbDocument();
        document.setSpaceId(spaceId);
        document.setTitle(title);

        if (isPdf) {
            document.setDocumentType("pdf");
        } else if (isMarkdown) {
            document.setDocumentType("markdown");
        } else {
            document.setDocumentType("txt");
        }

        document.setOriginalFilename(safeFilename);
        document.setStoragePath("uploaded://" + safeFilename);
        document.setParseStatus("PARSING");
        document.setChunkCount(0);
        document.setStatus(1);

        KbDocument saved = kbDocumentRepository.save(document);

        List<KbChunkResponse> chunks;

        if (isPdf) {
            AiPdfParseResponse parseResponse = aiPdfParseClient.parsePdf(file);

            if (parseResponse == null
                    || parseResponse.getChunks() == null
                    || parseResponse.getChunks().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pdf parse result is empty");
            }

            List<KbDocumentChunk> pdfChunks = savePdfChunks(spaceId, saved, parseResponse.getChunks());

            saved.setParseStatus("PARSED");
            saved.setChunkCount(pdfChunks.size());
            kbDocumentRepository.save(saved);

            // PDF chunk 已经保存到 kb_document_chunk。
            // 为避免 KbChunkResponse 构造器不匹配，这里先返回空列表。
            // 后续通过 GET /api/kb/documents/{id}/chunks 查看 chunk。
            chunks = List.of();

        } else {
            String content;
            try {
                content = new String(file.getBytes(), StandardCharsets.UTF_8);
                content = removeBom(content);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "failed to read uploaded file");
            }

            if (content.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document content cannot be blank");
            }

            chunks = kbChunkService.parseDocument(
                    saved.getId(),
                    new ParseKbDocumentRequest(content, chunkSize)
            );
        }

        Integer currentCount = space.getDocumentCount() == null ? 0 : space.getDocumentCount();
        space.setDocumentCount(currentCount + 1);
        kbSpaceRepository.save(space);

        Integer indexedCount = 0;
        String vectorStatus = "SUCCESS";

        try {
            VectorizeKbDocumentResponse vectorizeResponse = kbVectorService.vectorizeDocument(saved.getId());

            if (vectorizeResponse != null && vectorizeResponse.getIndexedCount() != null) {
                indexedCount = vectorizeResponse.getIndexedCount();
            }
        } catch (Exception e) {
            vectorStatus = "FAILED";
            e.printStackTrace();
        }

        KbDocument parsedDocument = kbDocumentRepository.findById(saved.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        return new UploadKbDocumentResponse(
                toResponse(parsedDocument),
                chunks,
                indexedCount,
                vectorStatus
        );
    }
    private List<KbDocumentChunk> savePdfChunks(Long spaceId,
                                                KbDocument document,
                                                List<AiPdfParseChunk> parsedChunks) {
        List<KbDocumentChunk> chunks = new ArrayList<>();

        int index = 0;

        for (AiPdfParseChunk parsedChunk : parsedChunks) {
            if (parsedChunk == null) {
                continue;
            }

            String content = parsedChunk.getContent();

            if (content == null || content.isBlank()) {
                continue;
            }

            KbDocumentChunk chunk = new KbDocumentChunk();
            chunk.setSpaceId(spaceId);
            chunk.setDocumentId(document.getId());
            chunk.setChunkIndex(index++);
            chunk.setPageNo(parsedChunk.getPageNo());
            chunk.setChunkType(parsedChunk.getChunkType() == null ? "TEXT" : parsedChunk.getChunkType());
            chunk.setSectionTitle(parsedChunk.getSectionTitle());
            chunk.setContent(content);
            chunk.setTokenCount(content.length());
            chunk.setStatus(1);

            chunks.add(kbDocumentChunkRepository.save(chunk));
        }

        if (chunks.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pdf parsed chunks are empty");
        }

        return chunks;
    }
    private String buildTitleFromFilename(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex <= 0) {
            return filename;
        }
        return filename.substring(0, dotIndex);
    }
    @Transactional
    @Override
    public void deleteDocument(Long id) {
        KbDocument document = kbDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found"));

        if (document.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        }

        document.setStatus(0);
        kbDocumentRepository.save(document);

        List<KbDocumentChunk> chunks = kbDocumentChunkRepository.findByDocumentIdAndStatus(id, 1);
        for (KbDocumentChunk chunk : chunks) {
            chunk.setStatus(0);
        }
        kbDocumentChunkRepository.saveAll(chunks);

        KbSpace space = kbSpaceRepository.findById(document.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "space not found"));

        Integer currentCount = space.getDocumentCount() == null ? 0 : space.getDocumentCount();
        space.setDocumentCount(Math.max(0, currentCount - 1));
        kbSpaceRepository.save(space);
    }
}