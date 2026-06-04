package com.cookie.askflowbackend.service;

import com.cookie.askflowbackend.dto.AiAskResponse;
import com.cookie.askflowbackend.dto.AiVectorSearchHit;
import com.cookie.askflowbackend.entity.RagTrace;
import com.cookie.askflowbackend.entity.RagTraceChunk;
import com.cookie.askflowbackend.repository.RagTraceChunkRepository;
import com.cookie.askflowbackend.repository.RagTraceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.cookie.askflowbackend.dto.RagRetrievedChunk;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RagTraceService {

    private final RagTraceRepository ragTraceRepository;
    private final RagTraceChunkRepository ragTraceChunkRepository;
    private final ObjectMapper objectMapper;

    public RagTraceService(RagTraceRepository ragTraceRepository,
                           RagTraceChunkRepository ragTraceChunkRepository,
                           ObjectMapper objectMapper) {
        this.ragTraceRepository = ragTraceRepository;
        this.ragTraceChunkRepository = ragTraceChunkRepository;
        this.objectMapper = objectMapper;
    }

    public void saveSuccessTrace(Long spaceId,
                                 Long sessionId,
                                 String question,
                                 String rewrittenQuery,
                                 List<AiVectorSearchHit> hits,
                                 AiAskResponse aiAskResponse,
                                 long retrievalTimeMs,
                                 long generationTimeMs,
                                 long totalTimeMs,
                                 int topK,
                                 double minScore) {
        try {
            RagTrace trace = new RagTrace();
            trace.setSpaceId(spaceId);
            trace.setSessionId(sessionId);
            trace.setQuestion(question);
            trace.setRewrittenQuery(rewrittenQuery);
            trace.setAnswer(aiAskResponse == null ? null : aiAskResponse.getAnswer());
            trace.setRetrievalMode("vector");
            trace.setTopK(topK);
            trace.setMinScore(minScore);
            trace.setContextCount(hits == null ? 0 : hits.size());
            trace.setRetrievalTimeMs(retrievalTimeMs);
            trace.setGenerationTimeMs(generationTimeMs);
            trace.setTotalTimeMs(totalTimeMs);
            trace.setRefused(isRefused(aiAskResponse));
            trace.setStatus("SUCCESS");
            trace.setCreatedAt(LocalDateTime.now());

            if (aiAskResponse != null) {
                trace.setCitationsJson(objectMapper.writeValueAsString(aiAskResponse.getCitations()));
                if (aiAskResponse.getDebug() != null && aiAskResponse.getDebug().get("retrieval_scores") != null) {
                    trace.setRetrievalScoresJson(objectMapper.writeValueAsString(aiAskResponse.getDebug().get("retrieval_scores")));
                }
            }

            RagTrace savedTrace = ragTraceRepository.save(trace);

            if (hits != null) {
                int rank = 1;
                for (AiVectorSearchHit hit : hits) {
                    RagTraceChunk traceChunk = new RagTraceChunk();
                    traceChunk.setTraceId(savedTrace.getId());
                    traceChunk.setChunkId(hit.getChunkId());
                    traceChunk.setDocumentId(hit.getDocumentId());
                    traceChunk.setDocumentTitle(hit.getDocumentTitle());
                    traceChunk.setChunkIndex(hit.getChunkIndex());
                    traceChunk.setRankNo(rank++);
                    traceChunk.setScore(hit.getScore());
                    traceChunk.setSelected(true);
                    traceChunk.setContentSnapshot(hit.getContent());
                    traceChunk.setCreatedAt(LocalDateTime.now());
                    ragTraceChunkRepository.save(traceChunk);
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to save RAG trace: " + e.getMessage());
        }
    }
    public void saveSuccessTraceFromRetrievedChunks(Long spaceId,
                                                    Long sessionId,
                                                    String question,
                                                    String rewrittenQuery,
                                                    List<RagRetrievedChunk> chunks,
                                                    AiAskResponse aiAskResponse,
                                                    long retrievalTimeMs,
                                                    long generationTimeMs,
                                                    long totalTimeMs,
                                                    int topK,
                                                    double minScore) {
        try {
            RagTrace trace = new RagTrace();
            trace.setSpaceId(spaceId);
            trace.setSessionId(sessionId);
            trace.setQuestion(question);
            trace.setRewrittenQuery(rewrittenQuery);
            trace.setAnswer(aiAskResponse == null ? null : aiAskResponse.getAnswer());
            trace.setRetrievalMode("hybrid");
            trace.setTopK(topK);
            trace.setMinScore(minScore);
            trace.setContextCount(chunks == null ? 0 : chunks.size());
            trace.setRetrievalTimeMs(retrievalTimeMs);
            trace.setGenerationTimeMs(generationTimeMs);
            trace.setTotalTimeMs(totalTimeMs);
            trace.setRefused(isRefused(aiAskResponse));
            trace.setStatus("SUCCESS");
            trace.setCreatedAt(LocalDateTime.now());

            if (aiAskResponse != null) {
                trace.setCitationsJson(objectMapper.writeValueAsString(aiAskResponse.getCitations()));
            }

            if (chunks != null) {
                List<Map<String, Object>> retrievalScores = chunks.stream()
                        .map(chunk -> {
                            Map<String, Object> item = new HashMap<>();
                            item.put("chunk_id", String.valueOf(chunk.getChunkId()));
                            item.put("document_name", chunk.getDocumentTitle());
                            item.put("score", chunk.getScore());
                            item.put("source", chunk.getSource());
                            return item;
                        })
                        .toList();

                trace.setRetrievalScoresJson(objectMapper.writeValueAsString(retrievalScores));
            }

            RagTrace savedTrace = ragTraceRepository.save(trace);

            if (chunks != null) {
                int rank = 1;
                for (RagRetrievedChunk chunk : chunks) {
                    RagTraceChunk traceChunk = new RagTraceChunk();
                    traceChunk.setTraceId(savedTrace.getId());
                    traceChunk.setChunkId(chunk.getChunkId());
                    traceChunk.setDocumentId(chunk.getDocumentId());
                    traceChunk.setDocumentTitle(chunk.getDocumentTitle());
                    traceChunk.setChunkIndex(chunk.getChunkIndex());
                    traceChunk.setRankNo(rank++);
                    traceChunk.setScore(chunk.getScore());
                    traceChunk.setSelected(true);
                    traceChunk.setContentSnapshot(chunk.getContent());
                    traceChunk.setCreatedAt(LocalDateTime.now());
                    ragTraceChunkRepository.save(traceChunk);
                }
            }

        } catch (Exception e) {
            System.out.println("Failed to save hybrid RAG trace: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private boolean isRefused(AiAskResponse response) {
        if (response == null || response.getAnswer() == null) {
            return true;
        }
        String answer = response.getAnswer();
        return answer.contains("没有检索到足够依据")
                || answer.contains("知识库中没有")
                || answer.contains("无法根据当前知识库");
    }
}