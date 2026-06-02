package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.RagTrace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagTraceRepository extends JpaRepository<RagTrace, Long> {

    List<RagTrace> findTop50BySpaceIdOrderByCreatedAtDesc(Long spaceId);
}