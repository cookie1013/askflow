package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.RagTraceChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagTraceChunkRepository extends JpaRepository<RagTraceChunk, Long> {

    List<RagTraceChunk> findByTraceIdOrderByRankNoAsc(Long traceId);
}