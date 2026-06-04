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
    private static final double HYBRID_BONUS = 0.05;

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
        boolean tableQuestion = isTableQuestion(question);

        int safeTopK = topK <= 0 ? 3 : topK;
        double safeMinScore = minScore < 0 ? 0.0 : minScore;

// 表格类问题：扩大候选召回，降低初筛阈值，让 TABLE chunk 有机会进入候选集
        int candidateTopK = tableQuestion ? 20 : safeTopK;
        double candidateMinScore = tableQuestion ? 0.0 : safeMinScore;

        AiVectorSearchRequest vectorSearchRequest = new AiVectorSearchRequest(
                spaceId,
                question,
                candidateTopK,
                candidateMinScore
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
                    "vector",
                    hit.getPageNo(),
                    hit.getChunkType(),
                    hit.getSectionTitle()

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
                        "keyword",
                        keywordChunk.getPageNo(),
                        keywordChunk.getChunkType(),
                        keywordChunk.getSectionTitle()
                );

                merged.put(chunkId, chunk);
            }
        }

        // 3. 排序 + topK 截断
        // 3. 内容去重 + 表格问题加权 + 排序 + topK 截断
        return deduplicateByContent(merged.values()).stream()
                .peek(chunk -> chunk.setScore(applyTableBoost(question, chunk)))
                .sorted(Comparator.comparing(RagRetrievedChunk::getScore).reversed())
                .limit(safeTopK)
                .toList();
    }

    private boolean isTableQuestion(String question) {
        if (question == null) {
            return false;
        }

        return question.contains("表格")
                || question.contains("矩阵")
                || question.contains("故障现象")
                || question.contains("可能原因")
                || question.contains("排查动作")
                || question.contains("期望处理");
    }
    private double applyTableBoost(String question, RagRetrievedChunk chunk) {
        double score = chunk.getScore() == null ? 0.0 : chunk.getScore();

        if (isTableQuestion(question) && "TABLE".equalsIgnoreCase(chunk.getChunkType())) {
            score += 0.25;
        }

        return score;
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
    private List<RagRetrievedChunk> deduplicateByContent(Collection<RagRetrievedChunk> chunks) {
        Map<String, RagRetrievedChunk> contentMap = new HashMap<>();

        for (RagRetrievedChunk chunk : chunks) {
            String key = normalizeContent(chunk.getContent());

            if (key == null || key.isBlank()) {
                key = "chunk:" + chunk.getChunkId();
            }

            RagRetrievedChunk existing = contentMap.get(key);

            if (existing == null || safeScore(chunk) > safeScore(existing)) {
                contentMap.put(key, chunk);
            }
        }

        return new ArrayList<>(contentMap.values());
    }

    private String normalizeContent(String content) {
        if (content == null) {
            return "";
        }

        return content
                .replaceAll("\\s+", "")
                .replaceAll("[\\p{Punct}，。！？；：、“”‘’（）【】《》]", "")
                .trim();
    }

    private double safeScore(RagRetrievedChunk chunk) {
        return chunk.getScore() == null ? 0.0 : chunk.getScore();
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