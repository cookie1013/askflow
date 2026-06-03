package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.RagEvalResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagEvalResultRepository extends JpaRepository<RagEvalResult, Long> {

    List<RagEvalResult> findBySpaceIdOrderByCreatedAtDesc(Long spaceId);

    List<RagEvalResult> findByCaseIdOrderByCreatedAtDesc(Long caseId);
}