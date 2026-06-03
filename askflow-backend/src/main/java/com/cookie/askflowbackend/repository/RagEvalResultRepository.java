package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.RagEvalResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RagEvalResultRepository extends JpaRepository<RagEvalResult, Long> {

    List<RagEvalResult> findBySpaceIdOrderByCreatedAtDesc(Long spaceId);

    List<RagEvalResult> findByCaseIdOrderByCreatedAtDesc(Long caseId);

    Optional<RagEvalResult> findFirstByCaseIdOrderByCreatedAtDesc(Long caseId);
}