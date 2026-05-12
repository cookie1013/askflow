package com.cookie.askflowbackend.repository;

import com.cookie.askflowbackend.entity.KbDocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbDocumentChunkRepository extends JpaRepository<KbDocumentChunk, Long> {

    List<KbDocumentChunk> findByDocumentIdAndStatusOrderByChunkIndexAsc(Long documentId, Integer status);

    long countByDocumentIdAndStatus(Long documentId, Integer status);
}