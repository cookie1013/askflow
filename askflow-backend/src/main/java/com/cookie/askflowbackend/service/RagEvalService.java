package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.client.AiClient;
import com.cookie.askflowbackend.client.AiVectorClient;
import com.cookie.askflowbackend.dto.*;
import com.cookie.askflowbackend.entity.RagEvalCase;
import com.cookie.askflowbackend.entity.RagEvalResult;
import com.cookie.askflowbackend.repository.RagEvalCaseRepository;
import com.cookie.askflowbackend.repository.RagEvalResultRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.cookie.askflowbackend.dto.RagRetrievedChunk;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RagEvalService {

    private static final int DEFAULT_TOP_K = 3;
    private static final double DEFAULT_MIN_SCORE = 0.35;

    private final RagEvalCaseRepository ragEvalCaseRepository;
    private final RagEvalResultRepository ragEvalResultRepository;
    private final AiVectorClient aiVectorClient;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final HybridRetrievalService hybridRetrievalService;
    public RagEvalService(RagEvalCaseRepository ragEvalCaseRepository,
                          RagEvalResultRepository ragEvalResultRepository,
                          AiVectorClient aiVectorClient,
                          AiClient aiClient,
                          ObjectMapper objectMapper,
                          HybridRetrievalService hybridRetrievalService) {
        this.ragEvalCaseRepository = ragEvalCaseRepository;
        this.ragEvalResultRepository = ragEvalResultRepository;
        this.aiVectorClient = aiVectorClient;
        this.aiClient = aiClient;
        this.objectMapper = objectMapper;
        this.hybridRetrievalService = hybridRetrievalService;
    }

    public RagEvalCase createCase(CreateRagEvalCaseRequest request) {
        RagEvalCase evalCase = new RagEvalCase();
        evalCase.setSpaceId(request.getSpaceId());
        evalCase.setQuestion(request.getQuestion());
        evalCase.setExpectedChunkIdsJson(toJson(defaultList(request.getExpectedChunkIds())));
        evalCase.setExpectedAnswerKeywordsJson(toJson(defaultList(request.getExpectedAnswerKeywords())));
        evalCase.setShouldRefuse(Boolean.TRUE.equals(request.getShouldRefuse()));
        evalCase.setCaseType(request.getCaseType() == null || request.getCaseType().isBlank()
                ? "normal"
                : request.getCaseType());
        evalCase.setStatus(1);
        evalCase.setCreatedAt(LocalDateTime.now());
        evalCase.setUpdatedAt(LocalDateTime.now());

        return ragEvalCaseRepository.save(evalCase);
    }

    public List<RagEvalCase> listCases(Long spaceId) {
        return ragEvalCaseRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1);
    }

    public RagEvalResult runCase(Long caseId) {
        RagEvalCase evalCase = ragEvalCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "eval case not found"));

        if (evalCase.getStatus() == null || evalCase.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "eval case not found");
        }

        long totalStart = System.currentTimeMillis();

        long retrievalStart = System.currentTimeMillis();

        List<RagRetrievedChunk> hits = hybridRetrievalService.retrieve(
                evalCase.getSpaceId(),
                evalCase.getQuestion(),
                DEFAULT_TOP_K,
                DEFAULT_MIN_SCORE
        );

        long retrievalTimeMs = System.currentTimeMillis() - retrievalStart;

        List<AiContextChunk> contextChunks = hits.stream()
                .map(hit -> new AiContextChunk(
                        String.valueOf(hit.getChunkId()),
                        hit.getDocumentTitle(),
                        hit.getContent(),
                        hit.getScore()
                ))
                .toList();

        long generationStart = System.currentTimeMillis();
        AiAskResponse askResponse = aiClient.ask(evalCase.getQuestion(), contextChunks);
        long generationTimeMs = System.currentTimeMillis() - generationStart;

        long totalTimeMs = System.currentTimeMillis() - totalStart;

        List<String> expectedChunkIds = readStringList(evalCase.getExpectedChunkIdsJson());
        List<String> expectedKeywords = readStringList(evalCase.getExpectedAnswerKeywordsJson());

        List<String> hitChunkIds = hits.stream()
                .map(hit -> String.valueOf(hit.getChunkId()))
                .toList();

        List<String> citationChunkIds = extractCitationChunkIds(askResponse);

        Boolean recallHit = expectedChunkIds.isEmpty()
                ? null
                : hasIntersection(expectedChunkIds, hitChunkIds);

        Boolean citationHit = expectedChunkIds.isEmpty()
                ? null
                : hasIntersection(expectedChunkIds, citationChunkIds);

        boolean shouldRefuse = Boolean.TRUE.equals(evalCase.getShouldRefuse());
        boolean refused = isRefused(askResponse, hits);

        Boolean refusalCorrect = shouldRefuse == refused;

        Boolean answerKeywordHit = expectedKeywords.isEmpty()
                ? null
                : containsAllKeywords(askResponse == null ? null : askResponse.getAnswer(), expectedKeywords);

        boolean passed = calculatePassed(
                shouldRefuse,
                refused,
                recallHit,
                citationHit,
                answerKeywordHit
        );

        RagEvalResult result = new RagEvalResult();
        result.setCaseId(evalCase.getId());
        result.setSpaceId(evalCase.getSpaceId());
        result.setQuestion(evalCase.getQuestion());
        result.setAnswer(askResponse == null ? null : askResponse.getAnswer());
        result.setHitChunkIdsJson(toJson(hitChunkIds));
        result.setCitationChunkIdsJson(toJson(citationChunkIds));
        result.setRetrievalScoresJson(toJson(extractRetrievalScores(askResponse, hits)));
        result.setRecallHit(recallHit);
        result.setCitationHit(citationHit);
        result.setRefusalCorrect(refusalCorrect);
        result.setAnswerKeywordHit(answerKeywordHit);
        result.setPassed(passed);
        result.setRetrievalTimeMs(retrievalTimeMs);
        result.setGenerationTimeMs(generationTimeMs);
        result.setTotalTimeMs(totalTimeMs);
        result.setCreatedAt(LocalDateTime.now());

        return ragEvalResultRepository.save(result);
    }
    public List<RagEvalResult> runAll(Long spaceId) {
        List<RagEvalCase> cases = ragEvalCaseRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1);

        List<RagEvalResult> results = new ArrayList<>();
        for (RagEvalCase evalCase : cases) {
            results.add(runCase(evalCase.getId()));
        }

        return results;
    }
    public RagEvalSummaryResponse summary(Long spaceId) {
        List<RagEvalCase> cases = ragEvalCaseRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1);

        List<RagEvalResult> latestResults = new ArrayList<>();
        for (RagEvalCase evalCase : cases) {
            ragEvalResultRepository.findFirstByCaseIdOrderByCreatedAtDesc(evalCase.getId())
                    .ifPresent(latestResults::add);
        }

        long caseTotal = cases.size();
        long evaluated = latestResults.size();
        long passed = latestResults.stream()
                .filter(result -> Boolean.TRUE.equals(result.getPassed()))
                .count();

        double passRate = evaluated == 0 ? 0.0 : round(passed * 1.0 / evaluated);

        double avgTotalTimeMs = latestResults.stream()
                .filter(result -> result.getTotalTimeMs() != null)
                .mapToLong(RagEvalResult::getTotalTimeMs)
                .average()
                .orElse(0.0);

        return new RagEvalSummaryResponse(
                caseTotal,
                evaluated,
                passed,
                passRate,
                Math.round(avgTotalTimeMs * 100.0) / 100.0
        );
    }

    public List<RagEvalResult> listResultsByCase(Long caseId) {
        return ragEvalResultRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }

    private boolean calculatePassed(boolean shouldRefuse,
                                    boolean refused,
                                    Boolean recallHit,
                                    Boolean citationHit,
                                    Boolean answerKeywordHit) {
        if (shouldRefuse) {
            return refused;
        }

        if (refused) {
            return false;
        }

        boolean recallOk = recallHit == null || recallHit;
        boolean citationOk = citationHit == null || citationHit;
        boolean keywordOk = answerKeywordHit == null || answerKeywordHit;

        return recallOk && citationOk && keywordOk;
    }

    /**
     * 判断当前回答是否属于“无依据拒答”。
     *
     * 注意：
     * 不要使用 answer.contains("知识库中没有") 这种宽泛规则，
     * 因为正常回答里也可能出现“知识库中没有更多具体步骤”这类表达。
     */
    private boolean isRefused(AiAskResponse response, List<RagRetrievedChunk> hits){
        if (hits == null || hits.isEmpty()) {
            return true;
        }

        if (response == null || response.getAnswer() == null || response.getAnswer().isBlank()) {
            return true;
        }

        String answer = response.getAnswer().trim();

        return answer.contains("没有检索到足够依据")
                || answer.contains("未检索到足够依据")
                || answer.contains("没有检索到相关依据")
                || answer.contains("未检索到相关依据")
                || answer.contains("无法根据当前知识库回答")
                || answer.contains("无法根据当前知识库")
                || answer.contains("当前知识库中没有相关内容")
                || answer.contains("知识库中没有相关内容")
                || answer.contains("没有足够信息回答")
                || answer.contains("没有足够依据回答")
                || answer.contains("无法回答该问题");
    }

    private boolean hasIntersection(List<String> expected, List<String> actual) {
        if (expected == null || expected.isEmpty() || actual == null || actual.isEmpty()) {
            return false;
        }

        Set<String> actualSet = new HashSet<>(actual);
        for (String item : expected) {
            if (item != null && actualSet.contains(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAllKeywords(String answer, List<String> keywords) {
        if (answer == null || answer.isBlank()) {
            return false;
        }

        if (keywords == null || keywords.isEmpty()) {
            return true;
        }

        for (String keyword : keywords) {
            if (keyword != null && !keyword.isBlank() && !answer.contains(keyword)) {
                return false;
            }
        }

        return true;
    }

    private List<String> extractCitationChunkIds(AiAskResponse response) {
        if (response == null || response.getCitations() == null) {
            return List.of();
        }

        List<Map<String, Object>> citations = objectMapper.convertValue(
                response.getCitations(),
                new TypeReference<List<Map<String, Object>>>() {
                }
        );

        List<String> chunkIds = new ArrayList<>();
        for (Map<String, Object> citation : citations) {
            Object chunkId = citation.get("chunk_id");
            if (chunkId == null) {
                chunkId = citation.get("chunkId");
            }
            if (chunkId != null) {
                chunkIds.add(String.valueOf(chunkId));
            }
        }

        return chunkIds;
    }

    private Object extractRetrievalScores(AiAskResponse response, List<RagRetrievedChunk> hits) {
        if (hits == null) {
            return List.of();
        }

        return hits.stream()
                .map(hit -> Map.of(
                        "chunk_id", String.valueOf(hit.getChunkId()),
                        "document_name", hit.getDocumentTitle(),
                        "score", hit.getScore(),
                        "source", hit.getSource()
                ))
                .toList();
    }

    private List<String> readStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<String> defaultList(List<String> list) {
        return list == null ? List.of() : list;
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "[]";
        }
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}