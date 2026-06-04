package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.client.AiVectorClient;
import com.cookie.askflowbackend.dto.AiVectorSearchHit;
import com.cookie.askflowbackend.dto.AiVectorSearchRequest;
import com.cookie.askflowbackend.dto.AiVectorSearchResponse;
import com.cookie.askflowbackend.dto.RagRetrievedChunk;
import com.cookie.askflowbackend.entity.KbDocument;
import com.cookie.askflowbackend.entity.KbDocumentChunk;
import com.cookie.askflowbackend.repository.KbDocumentChunkRepository;
import com.cookie.askflowbackend.repository.KbDocumentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HybridRetrievalService {

    private static final double KEYWORD_BASE_SCORE = 0.45;
    private static final double HYBRID_BONUS = 0.10;

    private static final List<String> DOMAIN_KEYWORDS = List.of(
            "Redis",
            "MySQL",
            "RocketMQ",
            "Sentinel",
            "PV",
            "缓存",
            "未命中",
            "回源",
            "回填",
            "数据库",
            "访问统计",
            "同步写库",
            "异步",
            "短链",
            "短码",
            "跳转",
            "失败",
            "禁用",
            "过期",
            "限流",
            "原始链接",
            "互斥锁",
            "逻辑过期",
            "缓存击穿"
    );

    private final AiVectorClient aiVectorClient;
    private final KbDocumentChunkRepository kbDocumentChunkRepository;
    private final KbDocumentRepository kbDocumentRepository;

    public HybridRetrievalService(AiVectorClient aiVectorClient,
                                  KbDocumentChunkRepository kbDocumentChunkRepository,
                                  KbDocumentRepository kbDocumentRepository) {
        this.aiVectorClient = aiVectorClient;
        this.kbDocumentChunkRepository = kbDocumentChunkRepository;
        this.kbDocumentRepository = kbDocumentRepository;
    }

    public List<RagRetrievedChunk> retrieve(Long spaceId,
                                            String question,
                                            int topK,
                                            double minScore) {
        Map<Long, RagRetrievedChunk> merged = new HashMap<>();
        Map<Long, String> documentTitleCache = new HashMap<>();

        // 1. 向量召回
        AiVectorSearchRequest vectorSearchRequest = new AiVectorSearchRequest(
                spaceId,
                question,
                topK,
                minScore
        );

        AiVectorSearchResponse vectorSearchResponse = aiVectorClient.search(vectorSearchRequest);

        List<AiVectorSearchHit> vectorHits =
                vectorSearchResponse == null || vectorSearchResponse.getHits() == null
                        ? List.of()
                        : vectorSearchResponse.getHits();

        for (AiVectorSearchHit hit : vectorHits) {
            RagRetrievedChunk chunk = new RagRetrievedChunk(
                    hit.getChunkId(),
                    hit.getDocumentId(),
                    hit.getDocumentTitle(),
                    hit.getChunkIndex(),
                    hit.getContent(),
                    hit.getScore(),
                    "vector"
            );

            merged.put(hit.getChunkId(), chunk);
        }

        // 2. 关键词召回：从完整问题中抽取领域关键词，再分别 LIKE 检索
        List<String> keywords = extractKeywords(question);

        for (String keyword : keywords) {
            List<KbDocumentChunk> keywordChunks = kbDocumentChunkRepository.searchByKeyword(
                    spaceId,
                    keyword,
                    PageRequest.of(0, topK)
            );

            for (KbDocumentChunk keywordChunk : keywordChunks) {
                Long chunkId = keywordChunk.getId();

                if (merged.containsKey(chunkId)) {
                    RagRetrievedChunk existing = merged.get(chunkId);
                    existing.setScore(existing.getScore() + HYBRID_BONUS);
                    existing.setSource("hybrid");
                    continue;
                }

                String documentTitle = getDocumentTitle(keywordChunk.getDocumentId(), documentTitleCache);

                RagRetrievedChunk chunk = new RagRetrievedChunk(
                        keywordChunk.getId(),
                        keywordChunk.getDocumentId(),
                        documentTitle,
                        keywordChunk.getChunkIndex(),
                        keywordChunk.getContent(),
                        KEYWORD_BASE_SCORE,
                        "keyword"
                );

                merged.put(chunkId, chunk);
            }
        }

        // 3. 排序 + topK 截断
        return merged.values().stream()
                .sorted(Comparator.comparing(RagRetrievedChunk::getScore).reversed())
                .limit(topK)
                .toList();
    }

    private List<String> extractKeywords(String question) {
        if (question == null || question.isBlank()) {
            return List.of();
        }

        String normalizedQuestion = question.trim();
        List<String> result = new ArrayList<>();

        for (String keyword : DOMAIN_KEYWORDS) {
            if (normalizedQuestion.contains(keyword)) {
                result.add(keyword);
            }
        }

        return result.stream()
                .distinct()
                .limit(5)
                .toList();
    }

    private String getDocumentTitle(Long documentId, Map<Long, String> documentTitleCache) {
        if (documentId == null) {
            return null;
        }

        if (documentTitleCache.containsKey(documentId)) {
            return documentTitleCache.get(documentId);
        }

        String title = kbDocumentRepository.findById(documentId)
                .map(KbDocument::getTitle)
                .orElse(null);

        documentTitleCache.put(documentId, title);
        return title;
    }
}