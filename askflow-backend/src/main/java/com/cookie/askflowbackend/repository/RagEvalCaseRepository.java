package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.RagEvalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagEvalCaseRepository extends JpaRepository<RagEvalCase, Long> {

    List<RagEvalCase> findBySpaceIdAndStatusOrderByCreatedAtDesc(Long spaceId, Integer status);
}