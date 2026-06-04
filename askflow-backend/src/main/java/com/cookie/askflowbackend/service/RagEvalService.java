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
import com.cookie.askflowbackend.dto.BatchCreateRagEvalCaseRequest;
import java.time.LocalDateTime;
import com.fasterxml.jackson.core.type.TypeReference;
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
        return runCase(caseId, "hybrid");
    }
    public RagEvalResult runCase(Long caseId, String retrievalMode) {
        String mode = normalizeRetrievalMode(retrievalMode);
        RagEvalCase evalCase = ragEvalCaseRepository.findById(caseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "eval case not found"));

        if (evalCase.getStatus() == null || evalCase.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "eval case not found");
        }

        long totalStart = System.currentTimeMillis();

        long retrievalStart = System.currentTimeMillis();

        List<RagRetrievedChunk> hits = retrieveForEval(
                mode,
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
        result.setRetrievalMode(mode);

        return ragEvalResultRepository.save(result);
    }
    public List<RagEvalResult> runAll(Long spaceId) {
        return runAll(spaceId, "hybrid");
    }

    public List<RagEvalResult> runAll(Long spaceId, String retrievalMode) {
        String mode = normalizeRetrievalMode(retrievalMode);

        List<RagEvalCase> cases = ragEvalCaseRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1);

        List<RagEvalResult> results = new ArrayList<>();
        for (RagEvalCase evalCase : cases) {
            results.add(runCase(evalCase.getId(), mode));
        }

        return results;
    }
    public List<RagEvalCase> createCases(BatchCreateRagEvalCaseRequest request) {
        if (request == null || request.getCases() == null || request.getCases().isEmpty()) {
            return List.of();
        }

        return request.getCases().stream()
                .map(this::createCase)
                .toList();
    }
    public RagEvalSummaryResponse summary(Long spaceId) {
        return summary(spaceId, "hybrid");
    }

    public RagEvalSummaryResponse summary(Long spaceId, String retrievalMode) {
        String mode = normalizeRetrievalMode(retrievalMode);

        List<RagEvalCase> cases = ragEvalCaseRepository.findBySpaceIdAndStatusOrderByCreatedAtDesc(spaceId, 1);

        List<RagEvalResult> latestResults = new ArrayList<>();
        Map<Long, RagEvalCase> caseMap = new HashMap<>();

        for (RagEvalCase evalCase : cases) {
            caseMap.put(evalCase.getId(), evalCase);

            ragEvalResultRepository.findFirstByCaseIdAndRetrievalModeOrderByCreatedAtDesc(evalCase.getId(), mode)
                    .ifPresent(latestResults::add);
        }

        long caseTotal = cases.size();
        long evaluated = latestResults.size();
        long passed = latestResults.stream()
                .filter(result -> Boolean.TRUE.equals(result.getPassed()))
                .count();

        double passRate = evaluated == 0 ? 0.0 : round(passed * 1.0 / evaluated);

        MetricAccumulator metrics = calculateMetrics(latestResults, caseMap);

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
                metrics.recallAtK,
                metrics.mrr,
                metrics.citationAccuracy,
                metrics.refusalAccuracy,
                metrics.misRecallRate,
                metrics.answerKeywordAccuracy,
                Math.round(avgTotalTimeMs * 100.0) / 100.0
        );
    }

    public List<RagEvalResult> listResultsByCase(Long caseId) {
        return ragEvalResultRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }
    private List<RagRetrievedChunk> retrieveForEval(String mode,
                                                    Long spaceId,
                                                    String question,
                                                    int topK,
                                                    double minScore) {
        if ("vector".equals(mode)) {
            AiVectorSearchRequest searchRequest = new AiVectorSearchRequest(
                    spaceId,
                    question,
                    topK,
                    minScore
            );

            AiVectorSearchResponse searchResponse = aiVectorClient.search(searchRequest);

            List<AiVectorSearchHit> vectorHits =
                    searchResponse == null || searchResponse.getHits() == null
                            ? List.of()
                            : searchResponse.getHits();

            return vectorHits.stream()
                    .map(hit -> new RagRetrievedChunk(
                            hit.getChunkId(),
                            hit.getDocumentId(),
                            hit.getDocumentTitle(),
                            hit.getChunkIndex(),
                            hit.getContent(),
                            hit.getScore(),
                            "vector"
                    ))
                    .toList();
        }

        return hybridRetrievalService.retrieve(
                spaceId,
                question,
                topK,
                minScore
        );
    }

    private String normalizeRetrievalMode(String retrievalMode) {
        if ("vector".equalsIgnoreCase(retrievalMode)) {
            return "vector";
        }

        return "hybrid";
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
    private MetricAccumulator calculateMetrics(List<RagEvalResult> results,
                                               Map<Long, RagEvalCase> caseMap) {
        long recallCaseCount = 0;
        long recallHitCount = 0;

        long citationCaseCount = 0;
        long citationHitCount = 0;

        long refusalCaseCount = 0;
        long refusalCorrectCount = 0;
        long misRecallCount = 0;

        long keywordCaseCount = 0;
        long keywordHitCount = 0;

        double reciprocalRankSum = 0.0;
        long mrrCaseCount = 0;

        for (RagEvalResult result : results) {
            RagEvalCase evalCase = caseMap.get(result.getCaseId());
            if (evalCase == null) {
                continue;
            }

            List<String> expectedChunkIds = readStringList(evalCase.getExpectedChunkIdsJson());
            List<String> expectedKeywords = readStringList(evalCase.getExpectedAnswerKeywordsJson());
            List<String> hitChunkIds = readStringList(result.getHitChunkIdsJson());
            List<String> citationChunkIds = readStringList(result.getCitationChunkIdsJson());

            boolean shouldRefuse = Boolean.TRUE.equals(evalCase.getShouldRefuse());

            if (!expectedChunkIds.isEmpty()) {
                recallCaseCount++;

                if (hasIntersection(expectedChunkIds, hitChunkIds)) {
                    recallHitCount++;
                }

                citationCaseCount++;
                if (hasIntersection(expectedChunkIds, citationChunkIds)) {
                    citationHitCount++;
                }

                mrrCaseCount++;
                reciprocalRankSum += reciprocalRank(expectedChunkIds, hitChunkIds);
            }

            if (shouldRefuse) {
                refusalCaseCount++;

                if (Boolean.TRUE.equals(result.getRefusalCorrect())) {
                    refusalCorrectCount++;
                }

                if (!hitChunkIds.isEmpty()) {
                    misRecallCount++;
                }
            }

            if (!expectedKeywords.isEmpty()) {
                keywordCaseCount++;

                if (Boolean.TRUE.equals(result.getAnswerKeywordHit())) {
                    keywordHitCount++;
                }
            }
        }

        double recallAtK = recallCaseCount == 0 ? 0.0 : round(recallHitCount * 1.0 / recallCaseCount);
        double mrr = mrrCaseCount == 0 ? 0.0 : round(reciprocalRankSum / mrrCaseCount);
        double citationAccuracy = citationCaseCount == 0 ? 0.0 : round(citationHitCount * 1.0 / citationCaseCount);
        double refusalAccuracy = refusalCaseCount == 0 ? 0.0 : round(refusalCorrectCount * 1.0 / refusalCaseCount);
        double misRecallRate = refusalCaseCount == 0 ? 0.0 : round(misRecallCount * 1.0 / refusalCaseCount);
        double answerKeywordAccuracy = keywordCaseCount == 0 ? 0.0 : round(keywordHitCount * 1.0 / keywordCaseCount);

        return new MetricAccumulator(
                recallAtK,
                mrr,
                citationAccuracy,
                refusalAccuracy,
                misRecallRate,
                answerKeywordAccuracy
        );
    }

    private double reciprocalRank(List<String> expectedChunkIds, List<String> hitChunkIds) {
        if (expectedChunkIds == null || expectedChunkIds.isEmpty()
                || hitChunkIds == null || hitChunkIds.isEmpty()) {
            return 0.0;
        }

        Set<String> expectedSet = new HashSet<>(expectedChunkIds);

        for (int i = 0; i < hitChunkIds.size(); i++) {
            if (expectedSet.contains(hitChunkIds.get(i))) {
                return 1.0 / (i + 1);
            }
        }

        return 0.0;
    }

    private static class MetricAccumulator {
        private final double recallAtK;
        private final double mrr;
        private final double citationAccuracy;
        private final double refusalAccuracy;
        private final double misRecallRate;
        private final double answerKeywordAccuracy;

        private MetricAccumulator(double recallAtK,
                                  double mrr,
                                  double citationAccuracy,
                                  double refusalAccuracy,
                                  double misRecallRate,
                                  double answerKeywordAccuracy) {
            this.recallAtK = recallAtK;
            this.mrr = mrr;
            this.citationAccuracy = citationAccuracy;
            this.refusalAccuracy = refusalAccuracy;
            this.misRecallRate = misRecallRate;
            this.answerKeywordAccuracy = answerKeywordAccuracy;
        }
    }
}